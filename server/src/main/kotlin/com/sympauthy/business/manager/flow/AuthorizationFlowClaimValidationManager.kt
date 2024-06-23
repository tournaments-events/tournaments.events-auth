package com.sympauthy.business.manager.flow

import com.sympauthy.business.exception.businessExceptionOf
import com.sympauthy.business.manager.ClaimManager
import com.sympauthy.business.manager.user.CollectedClaimManager
import com.sympauthy.business.manager.validationcode.ValidationCodeManager
import com.sympauthy.business.model.code.ValidationCode
import com.sympauthy.business.model.code.ValidationCodeMedia
import com.sympauthy.business.model.code.ValidationCodeReason
import com.sympauthy.business.model.code.ValidationCodeReason.EMAIL_CLAIM
import com.sympauthy.business.model.code.ValidationCodeReason.PHONE_NUMBER_CLAIM
import com.sympauthy.business.model.oauth2.AuthorizeAttempt
import com.sympauthy.business.model.user.CollectedClaim
import com.sympauthy.business.model.user.User
import com.sympauthy.business.model.user.claim.Claim
import io.micronaut.http.HttpStatus.BAD_REQUEST
import jakarta.inject.Inject
import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import kotlinx.coroutines.coroutineScope

/**
 * Component in charge of validating the claim collected during the authorization flow.
 */
@Singleton
open class AuthorizationFlowClaimValidationManager(
    @Inject private val claimManager: ClaimManager,
    @Inject private val collectedClaimManager: CollectedClaimManager,
    @Inject private val validationCodeManager: ValidationCodeManager,
) {


    /**
     * List of all [ValidationCodeReason] why this manager can send validation code to the user.
     * The list also contain reasons which this authorization server is not able to send a validation code for.
     */
    val validationCodeReasons: List<ValidationCodeReason> = listOf(EMAIL_CLAIM, PHONE_NUMBER_CLAIM)

    /**
     * Return the claim validated by the [reason], null if the [reason] actually does not validate a claim.
     */
    fun getClaimValidatedBy(reason: ValidationCodeReason): Claim? {
        val claimId = when (reason) {
            EMAIL_CLAIM -> EMAIL_CLAIM.media.claim
            else -> null
        }
        return claimId?.let(claimManager::findById)
    }

    /**
     * Return the list of reason why we must send validation code to the user.
     * The list will only contain reason which this authorization server is able to send a validation code for.
     * ex. the authorization server cannot verify an email if there is no email sending solution configured.
     */
    fun getRequiredValidationCodeReasons(
        collectedClaims: List<CollectedClaim>
    ): List<ValidationCodeReason> {
        return getUnfilteredValidationCodeReasons(collectedClaims)
            .filter { validationCodeManager.canSendValidationCodeForReason(it) }
    }

    /**
     * Return the list of reason why we must send validation code to the user.
     * The list may contain reasons which this authorization server is not able to send a validation code for.
     */
    internal fun getUnfilteredValidationCodeReasons(
        collectedClaims: List<CollectedClaim>
    ): List<ValidationCodeReason> {
        return validationCodeReasons.mapNotNull { reason ->
            getClaimValidatedBy(reason)?.let { claim ->
                val collectedClaim = collectedClaims.firstOrNull { it.claim.id == claim.id }
                if (collectedClaim?.verified != true) reason else null
            }
        }
    }

    /**
     * Get the validation codes to validate the claims collected for the [user].
     */
    @Transactional
    open suspend fun getOrSendValidationCodes(
        authorizeAttempt: AuthorizeAttempt,
        user: User
    ): List<ValidationCode> = coroutineScope {
        val collectedClaims = collectedClaimManager.findClaimsReadableByAttempt(
            authorizeAttempt = authorizeAttempt
        )
        val reasons = getRequiredValidationCodeReasons(
            collectedClaims = collectedClaims
        )

        val existingCodes = validationCodeManager.findCodeForReasonsDuringAttempt(
            authorizeAttempt = authorizeAttempt,
            reasons = reasons
        )

        if (existingCodes.flatMap { it.reasons }.toSet() == reasons.toSet()) {
            // Only in case of prefect match we will reuse the existing validation codes
            existingCodes
        } else if (reasons.isNotEmpty()) {
            validationCodeManager.revokeValidationCodes(existingCodes)
            validationCodeManager.queueRequiredValidationCodes(
                user = user,
                authorizeAttempt = authorizeAttempt,
                reasons = reasons,
                collectedClaims = collectedClaims
            )
        } else {
            // No validation code to send to the user.
            emptyList()
        }
    }

    @Transactional
    open suspend fun validateClaimsByCode(
        authorizeAttempt: AuthorizeAttempt,
        media: ValidationCodeMedia,
        code: String
    ) {
        val validationCode = findCodeSendByMediaDuringAttempt(
            authorizeAttempt = authorizeAttempt,
            media = media
        )

        if (validationCode == null) {
            return
        }
        if (validationCode.code != code) {
            throw businessExceptionOf(
                detailsId = "flow.claim_validation.invalid_code",
                recommendedStatus = BAD_REQUEST
            )
        }

        val claims = validationCode.reasons.mapNotNull(this::getClaimValidatedBy)
        collectedClaimManager.validateClaims(
            userId = authorizeAttempt.userId!!,
            claims = claims
        )
    }

    /**
     * Return the code we have sent to the user to validate a claim using the provided [media] during the
     * [authorizeAttempt].
     *
     * This method ignores return codes that have been sent for other reason like resetting user password, etc.
     */
    internal suspend fun findCodeSendByMediaDuringAttempt(
        authorizeAttempt: AuthorizeAttempt,
        media: ValidationCodeMedia
    ): ValidationCode? {
        val codes = validationCodeManager.findCodeForReasonsDuringAttempt(
            authorizeAttempt = authorizeAttempt,
            reasons = validationCodeReasons
        )
        return codes.firstOrNull { it.media == media }
    }
}
