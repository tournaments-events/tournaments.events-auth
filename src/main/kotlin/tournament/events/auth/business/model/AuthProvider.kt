package tournament.events.auth.business.model

import io.micronaut.security.oauth2.client.OauthClient
import tournament.events.auth.business.model.ExternalAuthProviderType.OAUTH2

sealed class AuthProvider(
    val name: String,
    val type: ExternalAuthProviderType
) {

}

class OAuth2Provider(
    val client: OauthClient
) : AuthProvider(client.name, OAUTH2) {

}
