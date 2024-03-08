package com.sympauthy.business.manager.provider

import com.sympauthy.business.exception.businessExceptionOf
import com.sympauthy.business.manager.auth.oauth2.Oauth2ProviderManager
import com.sympauthy.business.model.oauth2.AuthorizeAttempt
import com.sympauthy.business.model.provider.EnabledProvider
import com.sympauthy.business.model.provider.config.ProviderOauth2Config
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus.INTERNAL_SERVER_ERROR
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class ProviderManager(
    @Inject private val oauth2ProviderManager: Oauth2ProviderManager
) {

    suspend fun authorizeWithProvider(
        provider: EnabledProvider,
        authorizeAttempt: AuthorizeAttempt
    ): HttpResponse<*> {
        return when {
            provider.auth is ProviderOauth2Config -> oauth2ProviderManager.authorizeWithProvider(
                provider,
                authorizeAttempt
            )

            else -> throw businessExceptionOf(
                INTERNAL_SERVER_ERROR, "exception.client.unsupported"
            )
        }
    }
}

