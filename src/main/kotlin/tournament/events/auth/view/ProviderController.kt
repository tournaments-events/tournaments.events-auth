package tournament.events.auth.view

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS
import jakarta.inject.Inject
import tournament.events.auth.business.manager.auth.AuthorizeStateManager
import tournament.events.auth.business.manager.auth.oauth2.Oauth2ProviderManager
import tournament.events.auth.business.manager.provider.ProviderConfigManager
import tournament.events.auth.business.manager.provider.ProviderManager

@Controller("/providers/{id}")
class ProviderController(
    @Inject private val authorizeStateManager: AuthorizeStateManager,
    @Inject private val oauth2ProviderManager: Oauth2ProviderManager,
    @Inject private val providerManager: ProviderManager,
    @Inject private val providerConfigManager: ProviderConfigManager
) {

    @Get("authorize")
    @Secured(IS_ANONYMOUS)
    suspend fun authorize(
        id: String,
        @QueryValue("state") serializedState: String?
    ): HttpResponse<*> {
        val authorizeAttempt = authorizeStateManager.verifyEncodedState(serializedState)
        val provider = providerConfigManager.findEnabledProviderById(id)
        return providerManager.authorizeWithProvider(provider, authorizeAttempt)
    }

    @Get("callback")
    @Secured(IS_ANONYMOUS)
    suspend fun callback(
        id: String,
        @QueryValue("code") code: String?,
        @QueryValue("error") error: String?,
        @QueryValue("error_description") errorDescription: String?,
        @QueryValue("state") serializedState: String?
    ): HttpResponse<*> {
        val provider = providerConfigManager.findEnabledProviderById(id)
        val redirect = oauth2ProviderManager.handleCallback(
            provider = provider,
            authorizeCode = code,
            error = error,
            errorDescription = errorDescription,
            state = serializedState
        )
        return HttpResponse.redirect<Any>(redirect.build())
    }
}
