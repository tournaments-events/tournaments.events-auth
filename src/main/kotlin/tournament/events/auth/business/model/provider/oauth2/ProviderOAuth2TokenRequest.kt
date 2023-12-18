package tournament.events.auth.business.model.provider.oauth2

import tournament.events.auth.business.model.provider.config.ProviderOauth2Config
import java.net.URI

/**
 * Business object containing all information to call the token endpoint and obtain access token from a third-party
 * provider.
 */
data class ProviderOAuth2TokenRequest(
    val oauth2: ProviderOauth2Config,
    val authorizeCode: String,
    val redirectUri: URI
)
