package tournament.events.auth.business.manager.auth.oauth2

import io.micronaut.http.HttpStatus.BAD_REQUEST
import io.r2dbc.spi.R2dbcDataIntegrityViolationException
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.business.exception.businessExceptionOf
import tournament.events.auth.business.manager.key.RandomKeyGenerator
import tournament.events.auth.business.mapper.AuthorizationCodeMapper
import tournament.events.auth.business.model.auth.AuthorizeAttempt
import tournament.events.auth.business.model.auth.oauth2.AuthorizationCode
import tournament.events.auth.data.model.AuthorizationCodeEntity
import tournament.events.auth.data.repository.AuthorizationCodeRepository
import java.time.LocalDateTime

@Singleton
class AuthorizationCodeManager(
    @Inject private val authorizeCodeRepository: AuthorizationCodeRepository,
    @Inject private val authorizationCodeMapper: AuthorizationCodeMapper,
    @Inject private val randomKeyGenerator: RandomKeyGenerator
) {

    suspend fun findByCode(code: String): AuthorizationCode? {
        return authorizeCodeRepository.findByCode(code)
            ?.let(authorizationCodeMapper::toAuthorizationCode)
    }

    suspend fun generateCode(
        authorizeAttempt: AuthorizeAttempt
    ): AuthorizationCode {
        val entity = AuthorizationCodeEntity(
            attemptId = authorizeAttempt.id,
            code = randomKeyGenerator.generateKey(),
            creationDate = LocalDateTime.now()
        )
        return try {
            authorizeCodeRepository.save(entity)
                .let(authorizationCodeMapper::toAuthorizationCode)
        } catch (e: R2dbcDataIntegrityViolationException) {
            throw businessExceptionOf(BAD_REQUEST, "description.oauth2.replay")
        }
    }
}
