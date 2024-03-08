package com.sympauthy.api.util

import com.sympauthy.business.manager.auth.oauth2.AuthorizeManager
import com.sympauthy.business.model.oauth2.AuthorizeAttempt
import com.sympauthy.config.model.UrlsConfig
import com.sympauthy.config.model.orThrow
import io.micronaut.http.HttpResponse
import io.micronaut.http.uri.UriBuilder
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class AuthorizationFlowRedirectBuilder(
    @Inject private val authorizeManager: AuthorizeManager,
    @Inject private val unauthorizedUrlsConfig: UrlsConfig
) {

    suspend fun redirectToSignIn(authorizeAttempt: AuthorizeAttempt): HttpResponse<*> {
        val encodedState = authorizeManager.encodeState(authorizeAttempt)
        return unauthorizedUrlsConfig.orThrow().flow.signIn.let(UriBuilder::of)
            .queryParam("state", encodedState)
            .build()
            .let { HttpResponse.temporaryRedirect<Any>(it) }
    }

    suspend fun redirectToError(
        errorCode: String?,
        details: String? = null,
        description: String? = null
    ): HttpResponse<*> {
        return unauthorizedUrlsConfig.orThrow().flow.error.let(UriBuilder::of)
            .apply {
                errorCode?.let { queryParam("error_code", it) }
                description?.let { queryParam("description", it) }
                details?.let { queryParam("details", it) }
            }
            .build()
            .let { HttpResponse.temporaryRedirect<Any>(it) }
    }
}
