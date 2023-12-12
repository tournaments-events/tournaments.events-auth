package tournament.events.auth.business.model.provider.config

import tournament.events.auth.business.model.provider.ProviderAuthType
import java.net.URI

sealed class ProviderAuth(
    val type: ProviderAuthType
)

class ProviderOauth2(
    val clientId: String,
    val clientSecret: String,
    val scopes: List<String>?,

    val authorizationUri: URI,

    val tokenUri: URI,
) : ProviderAuth(ProviderAuthType.OAUTH2)
