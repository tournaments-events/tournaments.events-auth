package tournament.events.auth.business.model.redirect

import io.micronaut.http.uri.UriBuilder
import tournament.events.auth.business.model.auth.oauth2.AuthorizeAttempt
import tournament.events.auth.business.model.auth.oauth2.AuthorizationCode
import java.net.URI

/**
 * Contains all the information to redirect the client back to client application that initiated the authentication
 * with this application.
 */
data class AuthorizeRedirect(
    val authorizeAttempt: AuthorizeAttempt,
    val authorizationCode: AuthorizationCode? = null,
    val error: String? = null,
    val errorDescription: String? = null
) {

    fun build(): URI {
        val builder = UriBuilder.of(authorizeAttempt.redirectUri)
        authorizeAttempt.state?.let { builder.queryParam("state", it) }
        if (authorizationCode != null) {
            authorizationCode.code.let { builder.queryParam("code", it) }
        } else {
            error?.let { builder.queryParam("error", it) }
            errorDescription?.let { builder.queryParam("error_description", it) }
        }
        return builder.build()
    }
}
