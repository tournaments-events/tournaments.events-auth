package tournament.events.auth.server.security

import io.micronaut.security.authentication.Authentication
import tournament.events.auth.business.model.client.Client

class ClientAuthentication(
    val client: Client
) : Authentication {
    override fun getName(): String = client.id

    override fun getRoles(): Collection<String> {
        return listOf("ROLE_CLIENT")
    }

    override fun getAttributes(): Map<String, Any> {
        return emptyMap()
    }
}
