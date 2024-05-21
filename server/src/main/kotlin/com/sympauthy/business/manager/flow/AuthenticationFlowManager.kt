package com.sympauthy.business.manager.flow

import com.sympauthy.business.manager.user.CollectedClaimManager
import com.sympauthy.business.manager.validationcode.ValidationCodeManager
import com.sympauthy.business.model.code.ValidationCode
import com.sympauthy.business.model.code.ValidationCodeReason
import com.sympauthy.business.model.oauth2.AuthorizeAttempt
import com.sympauthy.business.model.user.CollectedClaim
import com.sympauthy.business.model.user.User
import com.sympauthy.business.model.user.claim.OpenIdClaim
import jakarta.inject.Inject
import jakarta.inject.Singleton

/**
 * Manager in charge of checking if the authentication flow of a user is completed.
 */
@Singleton
class AuthenticationFlowManager(
    @Inject private val collectedClaimManager: CollectedClaimManager,
    @Inject private val validationCodeManager: ValidationCodeManager
) {

    /**
     * Queue the sending of [ValidationCode]s to validate the [collectedClaims] listed.
     */
    suspend fun queueRequiredValidationCodes(
        user: User,
        authorizeAttempt: AuthorizeAttempt,
        collectedClaims: List<CollectedClaim>
    ): List<ValidationCode> {
        val reasons = getRequiredValidationCodeReasons(
            collectedClaims = collectedClaims
        )
        return if (reasons.isNotEmpty()) {
            validationCodeManager.queueRequiredValidationCodes(
                user = user,
                authorizeAttempt = authorizeAttempt,
                reasons = reasons
            )
        } else emptyList()
    }

    /**
     * Return the list of code validation type that must be sent to the end-user.
     */
    internal fun getRequiredValidationCodeReasons(
        collectedClaims: List<CollectedClaim>
    ): List<ValidationCodeReason> {
        val reasons = mutableListOf<ValidationCodeReason>()

        // Validate user email.
        val emailClaim = collectedClaims.firstOrNull { it.claim.id == OpenIdClaim.EMAIL.id }
        if (emailClaim != null && emailClaim.verified != true) {
            reasons.add(ValidationCodeReason.EMAIL_CLAIM)
        }

        return reasons.filter { validationCodeManager.canSendValidationCodeForReason(it) }
    }

    suspend fun checkIfAuthenticationIsComplete(
        user: User,
        collectedClaims: List<CollectedClaim>
    ): AuthenticationFlowResult {
        val missingRequiredClaims = !collectedClaimManager.areAllRequiredClaimCollected(collectedClaims)
        val missingValidation = getRequiredValidationCodeReasons(collectedClaims).isNotEmpty()

        return AuthenticationFlowResult(
            user = user,
            missingRequiredClaims = missingRequiredClaims,
            missingValidation = missingValidation
        )
    }
}

/**
 * The result of the authentication flow.
 */
data class AuthenticationFlowResult(
    /**
     * The user that has been authentication during the authentication flow.
     */
    val user: User,
    /**
     * True if we are missing some required claims from the end-user and they must be collected by the client.
     */
    val missingRequiredClaims: Boolean,
    /**
     * True if some claims requires a validation by the end-user.
     */
    val missingValidation: Boolean,
) {

    /**
     * True if the sign-up is complete and the user can be redirected to the client.
     */
    val complete: Boolean = listOf(
        missingRequiredClaims,
        missingValidation
    ).none { it }
}
