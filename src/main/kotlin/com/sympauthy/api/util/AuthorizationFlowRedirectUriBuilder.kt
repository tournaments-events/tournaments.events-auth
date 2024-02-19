package com.sympauthy.api.util

import com.sympauthy.business.manager.SignUpResult
import com.sympauthy.business.manager.auth.oauth2.AuthorizationCodeManager
import com.sympauthy.business.model.oauth2.AuthorizeAttempt
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.net.URI

@Singleton
class AuthorizationFlowRedirectUriBuilder(
    @Inject private val authorizationCodeManager: AuthorizationCodeManager
) {

    /**
     * Return the URI where the end-user must be redirected to according to the [result].
     */
    suspend fun getRedirectUri(
        attempt: AuthorizeAttempt,
        result: SignUpResult
    ): URI {
        return when {
            result.complete -> getRedirectUriToClient(attempt)
            else -> TODO()
        }
    }

    /**
     * Generate a URI that will redirect the end-user to the client with an authorization code that the client
     * will be able to exchange against the tokens.
     */
    private suspend fun getRedirectUriToClient(attempt: AuthorizeAttempt): URI {
        return AuthorizeRedirect(
            authorizeAttempt = attempt,
            authorizationCode = authorizationCodeManager.generateCode(attempt)
        ).build()
    }
}
