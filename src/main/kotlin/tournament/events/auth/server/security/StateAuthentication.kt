package tournament.events.auth.server.security

import io.micronaut.security.authentication.Authentication
import tournament.events.auth.business.model.oauth2.AuthorizeAttempt

class StateAuthentication(
    val authorizeAttempt: AuthorizeAttempt
) : Authentication {
    override fun getName(): String = client.id

    override fun getRoles(): Collection<String> {
        return listOf(SecurityRule.HAS_VALID_STATE)
    }

    override fun getAttributes(): Map<String, Any> {
        return emptyMap()
    }
}
