package tournament.events.auth.business.manager.auth

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus.BAD_REQUEST
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.business.exception.businessExceptionOf
import tournament.events.auth.business.manager.jwt.JwtManager
import tournament.events.auth.business.mapper.AuthorizeAttemptMapper
import tournament.events.auth.business.model.auth.AuthorizeAttempt
import tournament.events.auth.data.model.AuthorizeAttemptEntity
import tournament.events.auth.data.repository.AuthorizeAttemptRepository
import java.util.*

/**
 * This class is in charge of handling the [state]()
 */
@Singleton
class AuthorizeStateManager(
    @Inject private val authorizeAttemptRepository: AuthorizeAttemptRepository,
    @Inject private val jwtManager: JwtManager,
    @Inject private val authorizeAttemptMapper: AuthorizeAttemptMapper
) {

    /**
     * Verify if the
     */
    suspend fun newAuthorizeAttempt(
        httpRequest: HttpRequest<*>,
        redirectUri: String,
        clientId: String,
        clientState: String? = null
    ): AuthorizeAttempt {
        checkIsExistingAttemptWithState(clientState)
        val entity = logAttempt(
            httpRequest = httpRequest,
            redirectUri = redirectUri,
            clientId = clientId,
            clientState = clientState,
        )
        return authorizeAttemptMapper.toAuthorizeAttempt(entity)
    }

    internal suspend fun checkIsExistingAttemptWithState(
        state: String?
    ) {
        val existingAttempt = state?.let {
            authorizeAttemptRepository.findByState(it)
        }
        if (existingAttempt != null) {
            throw businessExceptionOf(BAD_REQUEST, "exception.authorize.replay")
        }
    }

    internal suspend fun logAttempt(
        httpRequest: HttpRequest<*>,
        redirectUri: String,
        clientId: String,
        clientState: String?,
    ): AuthorizeAttemptEntity {
        val attempt = AuthorizeAttemptEntity(
            clientId = clientId,
            redirectUri = redirectUri,
            state = clientState
        )
        return authorizeAttemptRepository.save(attempt)
    }

    suspend fun encodeState(authorizeAttempt: AuthorizeAttempt): String {
        return jwtManager.create(STATE_KEY_NAME) {
            withKeyId(STATE_KEY_NAME)
            withSubject(authorizeAttempt.id.toString())
        }
    }

    suspend fun verifyEncodedState(state: String?): AuthorizeAttempt {
        if (state == null) {
            throw businessExceptionOf(BAD_REQUEST, "exception.authorize_state.missing_state")
        }
        val jwt = jwtManager.decodeAndVerify(STATE_KEY_NAME, state) ?: throw businessExceptionOf(
            BAD_REQUEST, "exception.authorize_state.invalid_state"
        )
        val attemptId = try {
            UUID.fromString(jwt.subject)
        } catch (e: IllegalArgumentException) {
            throw businessExceptionOf(
                BAD_REQUEST, "exception.authorize_state.invalid_state"
            )
        }
        val authorizeAttemptEntity = authorizeAttemptRepository.findById(attemptId) ?: throw businessExceptionOf(
            BAD_REQUEST, "exception.authorize_state.invalid_state"
        )
        return authorizeAttemptMapper.toAuthorizeAttempt(authorizeAttemptEntity)
    }

    companion object {
        /**
         * Name of the cryptographic key used to sign the state.
         */
        const val STATE_KEY_NAME = "state"
    }
}
