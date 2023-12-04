package tournament.events.auth.business.model.oauth2

import io.micronaut.http.uri.UriBuilder
import java.net.URI

data class AuthorizeRedirectUri(
    val authorizeUri: URI,
    val clientId: String,
    val scopes: List<String>?,
    val state: String?,
    val redirectUri: URI?,
) {

    val uri: URI
        get() {
            val builder = UriBuilder.of(authorizeUri)
            builder.queryParam("client_id", clientId)

            return builder.build()
        }
}
