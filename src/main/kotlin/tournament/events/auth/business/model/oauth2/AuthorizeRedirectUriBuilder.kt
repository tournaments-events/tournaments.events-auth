package tournament.events.auth.business.model.oauth2

import io.micronaut.http.uri.UriBuilder
import java.net.URI

data class AuthorizeRedirectUriBuilder(
    val authorizeUri: URI,
    val responseType: String,
    val clientId: String,
    val clientSecret: String?,
    val scopes: List<String>?,
    val state: String?,
    val redirectUri: URI?,
) {

    fun build(): URI {
        val builder = UriBuilder.of(authorizeUri)
        responseType.let { builder.queryParam("response_type", it) }
        clientId.let { builder.queryParam("client_id", it) }
        clientSecret.let { builder.queryParam("client_secret", it) }
        scopes?.joinToString(", ")?.let { builder.queryParam("scope", it) }
        state?.let { builder.queryParam("state", it) }
        redirectUri?.toString()?.let { builder.queryParam("redirect_uri", it) }
        return builder.build()
    }
}
