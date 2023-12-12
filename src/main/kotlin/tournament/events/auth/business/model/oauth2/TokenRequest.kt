package tournament.events.auth.business.model.oauth2

import tournament.events.auth.business.model.provider.config.ProviderOauth2
import java.net.URI

/**
 * Business object containing all information to call the token endpoint and obtain access token from a third-party
 * provider.
 */
data class TokenRequest(
    val oauth2: ProviderOauth2,
    val authorizeCode: String,
    val redirectUri: URI
)
