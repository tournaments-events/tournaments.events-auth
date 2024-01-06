package tournament.events.auth.security

import io.micronaut.http.HttpStatus.FORBIDDEN
import io.micronaut.security.authentication.Authentication
import tournament.events.auth.business.model.oauth2.AuthorizeAttempt
import tournament.events.auth.exception.httpExceptionOf
import tournament.events.auth.security.SecurityRule.HAS_VALID_STATE

class StateAuthentication(
    val authorizeAttempt: AuthorizeAttempt
) : Authentication {
    override fun getName(): String = client.id

    override fun getRoles(): Collection<String> {
        return listOf(HAS_VALID_STATE)
    }

    override fun getAttributes(): Map<String, Any> {
        return emptyMap()
    }
}

val Authentication.stateAuthentication: StateAuthentication
    get() = when (this) {
        is StateAuthentication -> this
        else -> throw httpExceptionOf(FORBIDDEN, "authentication.wrong")
    }

val Authentication.authorizeAttempt: AuthorizeAttempt
    get() = stateAuthentication.authorizeAttempt
