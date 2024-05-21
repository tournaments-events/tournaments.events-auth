package com.sympauthy.business.manager.validationcode

import com.sympauthy.business.model.code.ValidationCode
import com.sympauthy.business.model.code.ValidationCodeMedia
import com.sympauthy.business.model.code.ValidationCodeReason
import com.sympauthy.business.model.oauth2.AuthorizeAttempt
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
        reasons: List<ValidationCodeReason>
    ): List<ValidationCode> {
        val typesByMediaMap = reasons.groupBy(ValidationCodeReason::media)

        // Verify we have a sender for all media
        val senders = typesByMediaMap.mapValues { (media, _) ->
            senders.firstOrNull { it.media == media }
                ?: throw IllegalArgumentException("No sender configured for media ${media}.")
        }

        val codes = typesByMediaMap.map { (media, reasons) ->
            validationCodeGenerator.generateValidationCode(
                user = user,
                media = media,
                reasons = reasons,
                authorizeAttempt = authorizeAttempt
            )
        }

        codes.forEach {
            senders[it.media]?.sendValidationCode(
                user = user,
                validationCode = it
            )
        }

        return codes
    }
}
