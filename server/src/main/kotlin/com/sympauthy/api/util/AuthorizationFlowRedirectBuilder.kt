package com.sympauthy.api.util

import com.sympauthy.business.manager.flow.WebAuthorizationFlowRedirectUriBuilder
import com.sympauthy.business.model.flow.WebAuthorizationFlow
import com.sympauthy.business.model.oauth2.AuthorizeAttempt
import io.micronaut.http.HttpResponse
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class AuthorizationFlowRedirectBuilder(
    @Inject private val webFlowRedirectBuilder: WebAuthorizationFlowRedirectUriBuilder
) {

    suspend fun redirectToSignIn(
        authorizeAttempt: AuthorizeAttempt,
        flow: WebAuthorizationFlow
    ): HttpResponse<*> {
        return HttpResponse.temporaryRedirect<Any>(
            webFlowRedirectBuilder.getSignInRedirectUri(
                authorizeAttempt = authorizeAttempt,
                flow = flow
            )
        )
    }

    fun redirectToError(
        flow: WebAuthorizationFlow,
        errorCode: String?,
        details: String? = null,
        description: String? = null
    ): HttpResponse<*> {
        return HttpResponse.temporaryRedirect<Any>(
            webFlowRedirectBuilder.getErrorUri(
                flow = flow,
                errorCode = errorCode,
                details = details,
                description = description
            )
        )
    }
}
