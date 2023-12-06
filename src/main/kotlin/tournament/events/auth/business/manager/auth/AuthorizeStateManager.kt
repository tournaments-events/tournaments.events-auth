package tournament.events.auth.business.manager.auth

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus.BAD_REQUEST
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.business.exception.businessExceptionOf
import tournament.events.auth.business.manager.jwt.JwtManager
import tournament.events.auth.business.model.oauth2.State
import tournament.events.auth.data.model.AuthorizeAttemptEntity
import tournament.events.auth.data.repository.AuthorizeAttemptRepository

/**
 * This class is in charge of handling the [state]()
 */
@Singleton
class AuthorizeStateManager(
    @Inject private val authorizeAttemptRepository: AuthorizeAttemptRepository,
    @Inject private val jwtManager: JwtManager
) {

    /**
     * Create a new internal [State] associated to the [clientState] provided by the third-party user.
     */
    suspend fun createState(
        httpRequest: HttpRequest<*>,
        redirectUri: String,
        clientId: String,
        clientState: String? = null
    ): State {
        checkIsExistingAttemptWithState(clientState)
        val attempt = logAttempt(
            httpRequest = httpRequest,
            redirectUri = redirectUri,
            clientId = clientId,
            clientState = clientState,
        )
        return State(
            id = attempt.id!!
        )
    }

    internal suspend fun checkIsExistingAttemptWithState(
        state: String?
    ) {
        val existingAttempt = state?.let {
            authorizeAttemptRepository.findByClientState(it)
        }
        if (existingAttempt != null) {
            throw businessExceptionOf(BAD_REQUEST, "exception.authorize.state_already_used")
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
            clientState = clientState
        )
        return authorizeAttemptRepository.save(attempt)
    }

    suspend fun encodeState(state: State): String {
        return jwtManager.create("state") {
            withAudience(state.id.toString())
        }
    }

    fun verifyEncodedState(state: String?): State {
        if (state == null) {
            throw businessExceptionOf(BAD_REQUEST, "exception.authorize_state.missing_state")
        }
        return State(
            TODO()
        )
    }
}
