package tournament.events.auth.business.model.provider.config

import tournament.events.auth.business.model.provider.ProviderAuthType
import java.net.URI

sealed class ProviderAuthConfig(
    val type: ProviderAuthType
)

class ProviderOauth2Config(
    val clientId: String,
    val clientSecret: String,
    val scopes: List<String>?,

    val authorizationUri: URI,

    val tokenUri: URI,
) : ProviderAuthConfig(ProviderAuthType.OAUTH2)
