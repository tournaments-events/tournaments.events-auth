package com.sympauthy.business.manager.auth.oauth2

import com.sympauthy.api.exception.oauth2ExceptionOf
import com.sympauthy.business.manager.key.RandomKeyGenerator
import com.sympauthy.business.mapper.AuthorizationCodeMapper
import com.sympauthy.business.model.oauth2.AuthorizationCode
import com.sympauthy.business.model.oauth2.AuthorizeAttempt
import com.sympauthy.business.model.oauth2.OAuth2ErrorCode.INVALID_REQUEST
import com.sympauthy.data.model.AuthorizationCodeEntity
import com.sympauthy.data.repository.AuthorizationCodeRepository
import io.r2dbc.spi.R2dbcDataIntegrityViolationException
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.time.LocalDateTime

@Singleton
class AuthorizationCodeManager(
    @Inject private val authorizeCodeRepository: AuthorizationCodeRepository,
    @Inject private val authorizationCodeMapper: AuthorizationCodeMapper,
    @Inject private val randomKeyGenerator: RandomKeyGenerator
) {

    suspend fun generateCode(
        authorizeAttempt: AuthorizeAttempt
    ): AuthorizationCode {
        val entity = AuthorizationCodeEntity(
            attemptId = authorizeAttempt.id,
            code = randomKeyGenerator.generateKey(),
            creationDate = LocalDateTime.now(),
            // We copy the expiration to simplify the cleanup code.
            expirationDate = authorizeAttempt.expirationDate
        )
        return try {
            authorizeCodeRepository.save(entity)
                .let(authorizationCodeMapper::toAuthorizationCode)
        } catch (e: R2dbcDataIntegrityViolationException) {
            throw oauth2ExceptionOf(INVALID_REQUEST, "code.already_generated", "description.oauth2.replay")
        }
    }

    suspend fun deleteCode(code: String) {
        authorizeCodeRepository.deleteByCode(code)
    }
}
