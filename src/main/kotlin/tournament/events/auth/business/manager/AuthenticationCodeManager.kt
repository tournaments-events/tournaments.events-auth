package tournament.events.auth.business.manager

import io.micronaut.http.HttpStatus
import io.micronaut.http.HttpStatus.BAD_REQUEST
import io.reactivex.rxjava3.core.Completable
import jakarta.inject.Singleton
import tournament.events.auth.business.exception.singleBusinessExceptionOf
import tournament.events.auth.data.model.LoginAttemptEntity
import tournament.events.auth.data.repository.LoginAttemptRepository

@Singleton
class AuthenticationCodeManager(
    private val loginAttemptRepository: LoginAttemptRepository
) {

    fun authorize(
        clientId: String,
        redirectUri: String,
        state: String
    ): Completable {
        return checkExistingLoginAttempt(state)
            .andThen(registerLoginAttempt(clientId, redirectUri, state))
    }

    internal fun checkExistingLoginAttempt(
        state: String
    ): Completable {
        return loginAttemptRepository.findByState(state)
            .switchIfEmpty(
                singleBusinessExceptionOf(BAD_REQUEST, "exception.authorize.state_already_used")
            )
            .ignoreElement()
    }

    internal fun registerLoginAttempt(
        clientId: String,
        redirectUri: String,
        state: String
    ): Completable {
        val attempt = LoginAttemptEntity(
            state = state,
            redirectUri = redirectUri
        )
        return loginAttemptRepository.insert(attempt)
            .ignoreElement()
    }
}
