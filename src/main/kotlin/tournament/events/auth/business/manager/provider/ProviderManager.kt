package tournament.events.auth.business.manager.provider

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus.INTERNAL_SERVER_ERROR
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.business.exception.businessExceptionOf
import tournament.events.auth.business.manager.auth.oauth2.Oauth2ProviderManager
import tournament.events.auth.business.model.oauth2.AuthorizeAttempt
import tournament.events.auth.business.model.provider.EnabledProvider
import tournament.events.auth.business.model.provider.config.ProviderOauth2Config

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

