package com.sympauthy.business.manager.validationcode

import com.sympauthy.business.manager.RandomGenerator
import com.sympauthy.business.mapper.ValidationCodeMapper
import com.sympauthy.business.model.code.ValidationCode
import com.sympauthy.business.model.code.ValidationCodeMedia
import com.sympauthy.business.model.code.ValidationCodeReason
import com.sympauthy.business.model.oauth2.AuthorizeAttempt
import com.sympauthy.business.model.user.User
import com.sympauthy.data.model.ValidationCodeEntity
import com.sympauthy.data.repository.ValidationCodeRepository
import com.sympauthy.util.loggerForClass
import jakarta.inject.Inject
import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import java.time.LocalDateTime

/**
 * Component in charge of the generation of validation code that will be sent to the end-user.
 */
@Singleton
open class ValidationCodeGenerator(
    @Inject private val validationCodeRepository: ValidationCodeRepository,
    @Inject private val validationCodeMapper: ValidationCodeMapper,
    @Inject private val randomGenerator: RandomGenerator
) {

    private val logger = loggerForClass()

    @Transactional
    open suspend fun generateValidationCode(
        user: User,
        media: ValidationCodeMedia,
        reasons: List<ValidationCodeReason>,
        authorizeAttempt: AuthorizeAttempt?
    ): ValidationCode {
        var tryCount = 0
        var savedEntity: ValidationCodeEntity? = null

        val expirationDate = when {
            authorizeAttempt != null -> authorizeAttempt.expirationDate
            else -> LocalDateTime.now().plusHours(1) // FIXME Allow to configure expiration date
        }

        while (savedEntity == null && tryCount < MAX_ATTEMPTS) {
            try {
                val entity = ValidationCodeEntity(
                    code = generateCode(),
                    userId = user.id,
                    media = media.name,
                    reasons = reasons.map(ValidationCodeReason::name).toTypedArray(),
                    attemptId = authorizeAttempt?.id,
                    expirationDate = expirationDate,
                )
                savedEntity = validationCodeRepository.save(entity)
            } catch (e: Exception) {
                logger.error("Failed to insert", e)
                tryCount++
            }
        }

        return savedEntity?.let(validationCodeMapper::toValidationCode) ?: throw IllegalStateException(
            "Unable to generate code in $tryCount attempts."
        )
    }

    internal fun generateCode(): String {
        val code = randomGenerator.generateInt(
            origin = VALICATION_CODE_ORIGIN,
            bound = VALIDATION_CODE_BOUND
        )
        return String.format(CODE_FORMAT, code)
    }

    companion object {
        /**
         * Number of times we will try to regenerate a code in case we have a collision.
         */
        private const val MAX_ATTEMPTS = 10
        private const val CODE_FORMAT = "%06d"
        private const val VALICATION_CODE_ORIGIN = 0
        private const val VALIDATION_CODE_BOUND = 100_000
    }
}
