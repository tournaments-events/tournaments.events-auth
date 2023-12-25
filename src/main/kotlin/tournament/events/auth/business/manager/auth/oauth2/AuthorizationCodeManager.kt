package tournament.events.auth.business.manager.auth.oauth2

import io.r2dbc.spi.R2dbcDataIntegrityViolationException
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.api.exception.oauth2ExceptionOf
import tournament.events.auth.business.manager.key.RandomKeyGenerator
import tournament.events.auth.business.mapper.AuthorizationCodeMapper
import tournament.events.auth.business.model.oauth2.AuthorizeAttempt
import tournament.events.auth.business.model.oauth2.AuthorizationCode
import tournament.events.auth.business.model.oauth2.OAuth2ErrorCode.INVALID_REQUEST
import tournament.events.auth.data.model.AuthorizationCodeEntity
import tournament.events.auth.data.repository.AuthorizationCodeRepository
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
