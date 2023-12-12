package tournament.events.auth.business.manager.auth

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus.INTERNAL_SERVER_ERROR
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.business.exception.businessExceptionOf
import tournament.events.auth.business.manager.auth.oauth2.Oauth2ProviderManager
import tournament.events.auth.business.model.provider.EnabledProvider
import tournament.events.auth.business.model.provider.config.ProviderOauth2
import tournament.events.auth.business.model.oauth2.State

@Singleton
class ProviderManager(
    @Inject private val oauth2ProviderManager: Oauth2ProviderManager
) {

    suspend fun authorizeWithProvider(
        provider: EnabledProvider,
        state: State
    ): HttpResponse<*> {
        return when {
            provider.auth is ProviderOauth2 -> oauth2ProviderManager.authorizeWithProvider(provider, state)
            else -> throw businessExceptionOf(
                INTERNAL_SERVER_ERROR, "exception.client.unsupported"
            )
        }
    }
}

