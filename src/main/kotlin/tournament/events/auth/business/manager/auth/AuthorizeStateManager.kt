package tournament.events.auth.business.manager.auth

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus.BAD_REQUEST
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.api.exception.oauth2ExceptionOf
import tournament.events.auth.business.exception.businessExceptionOf
import tournament.events.auth.business.manager.jwt.JwtManager
import tournament.events.auth.business.mapper.AuthorizeAttemptMapper
import tournament.events.auth.business.model.auth.AuthorizeAttempt
import tournament.events.auth.business.model.auth.oauth2.OAuth2ErrorCode
import tournament.events.auth.business.model.auth.oauth2.OAuth2ErrorCode.INVALID_REQUEST
import tournament.events.auth.data.model.AuthorizeAttemptEntity
import tournament.events.auth.data.repository.AuthorizeAttemptRepository
import tournament.events.auth.util.toAbsoluteUri
import java.net.URI
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

    suspend fun newAuthorizeAttempt(
        clientId: String,
        uncheckedRedirectUri: String?,
        clientState: String? = null,
    ): AuthorizeAttempt {
        val redirectUri = checkRedirectUri(uncheckedRedirectUri)
        checkIsExistingAttemptWithState(clientState)

        // TODO: check client and redirect uri

        val entity = AuthorizeAttemptEntity(
            clientId = clientId,
            redirectUri = redirectUri.toString(),
            state = clientState
        )
        authorizeAttemptRepository.save(entity)

        return authorizeAttemptMapper.toAuthorizeAttempt(entity)
    }

    internal suspend fun checkIsExistingAttemptWithState(
        state: String?
    ) {
        val existingAttempt = state?.let {
            authorizeAttemptRepository.findByState(it)
        }
        if (existingAttempt != null) {
            throw oauth2ExceptionOf(INVALID_REQUEST, "description.oauth2.replay", "authorize.existing_state")
        }
    }

    internal fun checkRedirectUri(redirectUri: String?): URI {
        return redirectUri.toAbsoluteUri() ?: throw oauth2ExceptionOf(INVALID_REQUEST, "authorize.invalid_redirect_uri")
    }

    internal fun checkClient(clientId: String, redirectUri: URI) {

    }

    suspend fun encodeState(authorizeAttempt: AuthorizeAttempt): String {
        return jwtManager.create(STATE_KEY_NAME) {
            withKeyId(STATE_KEY_NAME)
            withSubject(authorizeAttempt.id.toString())
        }
    }

    suspend fun verifyEncodedState(state: String?): AuthorizeAttempt {
        if (state == null) {
            throw businessExceptionOf(BAD_REQUEST, "authorize_state.missing_state")
        }
        val jwt = jwtManager.decodeAndVerify(STATE_KEY_NAME, state) ?: throw businessExceptionOf(
            BAD_REQUEST, "description.oauth2.invalid_state"
        )
        val attemptId = try {
            UUID.fromString(jwt.subject)
        } catch (e: IllegalArgumentException) {
            throw businessExceptionOf(
                BAD_REQUEST, "description.oauth2.invalid_state"
            )
        }
        val authorizeAttemptEntity = authorizeAttemptRepository.findById(attemptId) ?: throw businessExceptionOf(
            BAD_REQUEST, "description.oauth2.invalid_state"
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
