package com.sympauthy.api.util

import com.sympauthy.business.model.oauth2.AuthorizationCode
import com.sympauthy.business.model.oauth2.AuthorizeAttempt
import io.micronaut.http.uri.UriBuilder
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
