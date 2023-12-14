package tournament.events.auth.business.model.oauth2

import io.micronaut.http.uri.UriBuilder
import tournament.events.auth.business.model.provider.config.ProviderOauth2Config
import java.net.URI

data class AuthorizationRequest(
    val oauth2: ProviderOauth2Config,
    val responseType: String,
    val state: String?,
    val redirectUri: URI?,
) {

    fun build(): URI {
        val builder = UriBuilder.of(oauth2.authorizationUri)
        responseType.let { builder.queryParam("response_type", it) }
        oauth2.clientId.let { builder.queryParam("client_id", it) }
        oauth2.clientSecret.let { builder.queryParam("client_secret", it) }
        oauth2.scopes?.joinToString(", ")?.let { builder.queryParam("scope", it) }
        state?.let { builder.queryParam("state", it) }
        redirectUri?.toString()?.let { builder.queryParam("redirect_uri", it) }
        return builder.build()
    }
}
