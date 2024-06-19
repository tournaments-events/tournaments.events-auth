package com.sympauthy.business.manager.validationcode

import com.sympauthy.business.mapper.ValidationCodeMapper
import com.sympauthy.business.model.code.ValidationCode
import com.sympauthy.business.model.code.ValidationCodeMedia
import com.sympauthy.business.model.code.ValidationCodeReason
import com.sympauthy.business.model.oauth2.AuthorizeAttempt
import com.sympauthy.business.model.user.CollectedClaim
import com.sympauthy.business.model.user.User
import com.sympauthy.data.repository.ValidationCodeRepository
import com.sympauthy.exception.localizedExceptionOf
import jakarta.inject.Inject
import jakarta.inject.Singleton
import jakarta.transaction.Transactional

@Singleton
open class ValidationCodeManager(
    @Inject private val validationCodeGenerator: ValidationCodeGenerator,
    @Inject private val validationCodeRepository: ValidationCodeRepository,
    @Inject private val senders: List<ValidationCodeMediaSender>,
    @Inject private val validationCodeMapper: ValidationCodeMapper
) {

    /**
     * Return the list of existing [ValidationCode] generated during the [authorizeAttempt] for one of the
     * provided [reasons].
     */
    suspend fun findCodeForReasonsDuringAttempt(
        authorizeAttempt: AuthorizeAttempt,
        reasons: List<ValidationCodeReason>,
        includesExpired: Boolean = false
    ): List<ValidationCode> {
        var sequence = validationCodeRepository
            .findByAttemptIdAndReasonsIn(
                attemptId = authorizeAttempt.id,
                reasons = reasons.map(ValidationCodeReason::name)
            )
            .asSequence()
            .map(validationCodeMapper::toValidationCode)
        if (!includesExpired) {
            sequence = sequence.filterNot(ValidationCode::expired)
        }
        return sequence.toList()
    }

    /**
     * Return true if this authorization server can send validation code to the end-user for the provided [reason].
     * False otherwise.
     */
    fun canSendValidationCodeForReason(reason: ValidationCodeReason): Boolean {
        return senders.any { it.media == reason.media }
    }

    /**
     * Queue the sending of [ValidationCode] of the provided [reasons].
     * If multiple reasons shares the same [ValidationCodeMedia], we will send only one code for those reasons.
     */
    @Transactional
    open suspend fun queueRequiredValidationCodes(
        user: User,
        authorizeAttempt: AuthorizeAttempt,
        collectedClaims: List<CollectedClaim>,
        reasons: List<ValidationCodeReason>
    ): List<ValidationCode> {
        val reasonsByMediaMap = reasons.groupBy(ValidationCodeReason::media)

        val senderByMediaMap = getSenderByMediaMap(
            medias = reasonsByMediaMap.keys,
            collectedClaims = collectedClaims
        )

        val codes = reasonsByMediaMap.map { (media, reasons) ->
            validationCodeGenerator.generateValidationCode(
                user = user,
                media = media,
                reasons = reasons,
                authorizeAttempt = authorizeAttempt
            )
        }

        codes.forEach {
            val sender = senderByMediaMap[it.media]!! // !! because it should never happen.
            sender.sender.sendValidationCode(
                user = user,
                collectedClaim = sender.collectedClaim,
                validationCode = it
            )
        }

        return codes
    }

    internal fun getSenderByMediaMap(
        medias: Set<ValidationCodeMedia>,
        collectedClaims: List<CollectedClaim>
    ): Map<ValidationCodeMedia, SenderClaimTuple> {
        return medias.associateWith { media ->
            val sender = senders.firstOrNull { it.media == media }
                ?: throw localizedExceptionOf(
                    "validationcode.missing_sender",
                    "media" to media
                )
            val claim = collectedClaims.firstOrNull { it.claim.id == media.claim }
                ?: throw localizedExceptionOf(
                    "validationcode.missing_claim",
                    "media" to media,
                    "claim" to media.claim
                )
            SenderClaimTuple(
                media = media,
                sender = sender,
                collectedClaim = claim
            )
        }
    }

    /**
     * Delete existing validation codes.
     */
    suspend fun revokeValidationCodes(codes: List<ValidationCode>) {
        codes.mapNotNull(ValidationCode::id)
            .let { validationCodeRepository.deleteByIds(it) }
    }

    internal data class SenderClaimTuple(
        val media: ValidationCodeMedia,
        val sender: ValidationCodeMediaSender,
        val collectedClaim: CollectedClaim
    )
}
