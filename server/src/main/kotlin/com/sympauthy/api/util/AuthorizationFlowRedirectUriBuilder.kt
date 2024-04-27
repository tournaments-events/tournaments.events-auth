package com.sympauthy.api.util

import com.sympauthy.business.manager.auth.oauth2.AuthorizationCodeManager
import com.sympauthy.business.manager.auth.oauth2.AuthorizeManager
import com.sympauthy.business.manager.flow.AuthenticationFlowResult
import com.sympauthy.business.model.oauth2.AuthorizeAttempt
import com.sympauthy.config.model.FlowUrlConfig
import com.sympauthy.config.model.UrlsConfig
import com.sympauthy.config.model.orThrow
import io.micronaut.http.uri.UriBuilder
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.net.URI

@Singleton
class AuthorizationFlowRedirectUriBuilder(
    @Inject private val authorizeManager: AuthorizeManager,
    @Inject private val authorizationCodeManager: AuthorizationCodeManager,
    @Inject private val uncheckedUrlsConfig: UrlsConfig
) {

    /**
     * Return the URI where the end-user must be redirected to according to the [result].
     */
    suspend fun getRedirectUri(
        attempt: AuthorizeAttempt,
        result: AuthenticationFlowResult
    ): URI {
        return when {
            result.missingRequiredClaims -> getRedirectUriToCollectClaims(attempt)
            result.complete -> getRedirectUriToClient(attempt)
            else -> TODO()
        }
    }

    /**
     * Generate a URI that will redirect the end-user to page of the flow collecting end-user claims.
     */
    private suspend fun getRedirectUriToCollectClaims(
        attempt: AuthorizeAttempt
    ): URI {
        return getFlowUri(attempt = attempt) { it.collectClaims }
    }

    /**
     * Generate a URI that will redirect the end-user to the client with an authorization code that the client
     * will be able to exchange against the tokens.
     */
    private suspend fun getRedirectUriToClient(
        attempt: AuthorizeAttempt
    ): URI {
        return ToClientRedirect(
            authorizeAttempt = attempt,
            authorizationCode = authorizationCodeManager.generateCode(attempt)
        ).build()
    }

    private suspend fun getFlowUri(
        attempt: AuthorizeAttempt,
        flowUri: (config: FlowUrlConfig) -> URI
    ): URI {
        val state = authorizeManager.encodeState(attempt)
        val flowUrlConfig = uncheckedUrlsConfig.orThrow().flow
        return flowUri(flowUrlConfig).let(UriBuilder::of)
            .queryParam("state", state)
            .build()
    }
}
