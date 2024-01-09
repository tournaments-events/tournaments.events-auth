package tournament.events.auth.business.model.redirect

import io.micronaut.http.uri.UriBuilder
import tournament.events.auth.business.model.provider.config.ProviderOauth2Config
import java.net.URI

/**
 * Contains all the information required to generate the URI where the user must be redirected to initiate
 * an authentication to a third-party provider.
 */
data class ProviderOauth2AuthorizationRedirect(
    val oauth2: ProviderOauth2Config,
    val responseType: String,
    val state: String?,
    val redirectUri: URI?,
) {

    fun build(): URI {
        val builder = UriBuilder.of(oauth2.authorizationUri)
        responseType.let { builder.queryParam("response_type", it) }
        oauth2.clientId.let { builder.queryParam("client_id", it) }
        oauth2.scopes?.joinToString(", ")?.let { builder.queryParam("scope", it) }
        state?.let { builder.queryParam("state", it) }
        redirectUri?.toString()?.let { builder.queryParam("redirect_uri", it) }
        return builder.build()
    }
}
