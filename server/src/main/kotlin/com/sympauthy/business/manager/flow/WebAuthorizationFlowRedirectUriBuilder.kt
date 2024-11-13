package com.sympauthy.business.manager.flow

import com.sympauthy.business.manager.auth.oauth2.AuthorizationCodeManager
import com.sympauthy.business.manager.auth.oauth2.AuthorizeManager
import com.sympauthy.business.model.flow.WebAuthorizationFlow
import com.sympauthy.business.model.oauth2.AuthorizeAttempt
import io.micronaut.http.uri.UriBuilder
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.net.URI

/**
 * Component in charge of constructing the URIs where the end-user will be redirected to continue its
 * authentication & authorization through a web authorization flow.
 */
@Singleton
class WebAuthorizationFlowRedirectUriBuilder(
    @Inject private val authorizeManager: AuthorizeManager,
    @Inject private val authorizationCodeManager: AuthorizationCodeManager
) {

    /**
     * Return the [URI] where the end-user must be redirected to start the authorization flow.
     */
    suspend fun getSignInRedirectUri(
        authorizeAttempt: AuthorizeAttempt,
        flow: WebAuthorizationFlow
    ): URI {
        return appendStateToUri(
            authorizeAttempt = authorizeAttempt,
            uri = flow.signInUri
        )
    }

    /**
     * Return the [URI] where the end-user must be redirected to according to the [result].
     */
    suspend fun getRedirectUri(
        authorizeAttempt: AuthorizeAttempt,
        flow: WebAuthorizationFlow,
        result: AuthorizationFlowResult
    ): URI {
        return when {
            result.missingRequiredClaims -> appendStateToUri(
                authorizeAttempt = authorizeAttempt,
                uri = flow.collectClaimsUri
            )

            result.missingMediaForClaimValidation.isNotEmpty() -> getRedirectUriToClaimValidation(
                authorizeAttempt = authorizeAttempt,
                flow = flow,
                result = result,
            )

            result.complete -> getRedirectUriToClient(
                authorizeAttempt = authorizeAttempt
            )

            else -> TODO()
        }
    }

    /**
     * Return the [URI] where the end-user must be redirected to validate
     */
    suspend fun getRedirectUriToClaimValidation(
        authorizeAttempt: AuthorizeAttempt,
        flow: WebAuthorizationFlow,
        result: AuthorizationFlowResult,
    ): URI {
        val uri = flow.validateClaimsUri.let(UriBuilder::of)
            .apply {
                result.missingMediaForClaimValidation.firstOrNull()?.let { queryParam("media", it.name) }
            }
            .build()
        return appendStateToUri(
            authorizeAttempt = authorizeAttempt,
            uri = uri,
        )
    }

    /**
     * Return the [URI] where the end-user must be redirected when there is an error during the authorization flow.
     */
    fun getErrorUri(
        flow: WebAuthorizationFlow,
        errorCode: String?,
        details: String? = null,
        description: String? = null
    ): URI {
        return flow.errorUri.let(UriBuilder::of)
            .apply {
                errorCode?.let { queryParam("error_code", it) }
                description?.let { queryParam("description", it) }
                details?.let { queryParam("details", it) }
            }
            .build()
    }

    /**
     * Return a [URI] redirecting the end-user to the client with an authorization code.
     * The authorization code may be exchanged for tokens by the client using the token endpoint.
     */
    internal suspend fun getRedirectUriToClient(
        authorizeAttempt: AuthorizeAttempt
    ): URI {
        val builder = UriBuilder.of(authorizeAttempt.redirectUri)
        authorizeAttempt.state
            ?.let { builder.queryParam("state", it) }
        authorizationCodeManager.generateCode(authorizeAttempt)
            .let { builder.queryParam("code", it.code) }
        return builder.build()
    }

    internal suspend fun appendStateToUri(
        authorizeAttempt: AuthorizeAttempt,
        uri: URI
    ): URI {
        val state = authorizeManager.encodeState(authorizeAttempt)
        return uri.let(UriBuilder::of)
            .queryParam("state", state)
            .build()
    }
}
