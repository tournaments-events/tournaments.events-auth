package com.sympauthy.business.manager.validationcode

import com.sympauthy.business.model.code.ValidationCode
import com.sympauthy.business.model.code.ValidationCodeMedia
import com.sympauthy.business.model.code.ValidationCodeReason
import com.sympauthy.business.model.oauth2.AuthorizeAttempt
import com.sympauthy.business.model.user.CollectedClaim
import com.sympauthy.business.model.user.User
import jakarta.inject.Inject
import jakarta.inject.Singleton
import jakarta.transaction.Transactional

@Singleton
open class ValidationCodeManager(
    @Inject private val validationCodeGenerator: ValidationCodeGenerator,
    @Inject private val senders: List<ValidationCodeMediaSender>
) {

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
        authorizeAttempt: AuthorizeAttempt?,
        collectedClaims: List<CollectedClaim>,
        reasons: List<ValidationCodeReason>
    ): List<ValidationCode> {
        val reasonsByMediaMap = reasons.groupBy(ValidationCodeReason::media)

        // Verify we have a sender for all media
        val senderByMediaMap = reasonsByMediaMap.mapValues { (media, _) ->
            val sender = senders.firstOrNull { it.media == media }
                ?: throw IllegalArgumentException("No sender configured for media ${media}.")
            val claim = collectedClaims.firstOrNull { it.claim.id == media.claim }
                ?: throw IllegalArgumentException("Missing required claim ${media.claim} for media ${media}.")
            Sender(
                media = media,
                sender = sender,
                claim = claim
            )
        }

        val codes = reasonsByMediaMap.map { (media, reasons) ->
            validationCodeGenerator.generateValidationCode(
                user = user,
                media = media,
                reasons = reasons,
                authorizeAttempt = authorizeAttempt
            )
        }

        codes.forEach {
            val sender = senderByMediaMap[it.media]
            sender?.sender?.sendValidationCode(
                user = user,
                collectedClaim = sender.claim,
                validationCode = it
            )
        }

        return codes
    }

    private data class Sender(
        val media: ValidationCodeMedia,
        val sender: ValidationCodeMediaSender,
        val claim: CollectedClaim
    )
}
