package tournament.events.auth.business.manager

import io.micronaut.http.HttpStatus.BAD_REQUEST
import io.reactivex.rxjava3.core.Completable
import jakarta.inject.Singleton
import tournament.events.auth.business.exception.completableBusinessExceptionOf
import tournament.events.auth.data.model.AuthorizeAttemptEntity
import tournament.events.auth.data.repository.AuthorizeAttemptRepository

@Singleton
class AuthorizationCodeManager(
    private val AuthorizeAttemptRepository: AuthorizeAttemptRepository
) {

    fun authorize(
        clientId: String,
        state: String
    ): Completable {
        return checkExistingAttempt(state)
            .andThen(registerAttempt(state))
    }

    internal fun checkExistingAttempt(
        state: String
    ): Completable {
        return AuthorizeAttemptRepository.findByState(state)
            .isEmpty
            .flatMapCompletable {
                if (!it) {
                    completableBusinessExceptionOf(BAD_REQUEST, "exception.authorize.state_already_used")
                } else {
                    Completable.complete()
                }
            }
    }

    internal fun registerAttempt(
        state: String
    ): Completable {
        val attempt = AuthorizeAttemptEntity(
            state = state,
            redirectUri = ""
        )
        return AuthorizeAttemptRepository.insert(attempt)
            .ignoreElement()
    }
}
