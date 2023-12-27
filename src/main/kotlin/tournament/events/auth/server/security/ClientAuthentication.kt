package tournament.events.auth.server.security

import io.micronaut.security.authentication.Authentication
import tournament.events.auth.api.exception.oauth2ExceptionOf
import tournament.events.auth.business.model.client.Client
import tournament.events.auth.business.model.oauth2.OAuth2ErrorCode.ACCESS_DENIED

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

val Authentication.clientAuthentication: ClientAuthentication
    get() = when (this) {
        is ClientAuthentication -> this
        else -> throw oauth2ExceptionOf(ACCESS_DENIED, "forbidden")
    }

val Authentication.client: Client
    get() = clientAuthentication.client
