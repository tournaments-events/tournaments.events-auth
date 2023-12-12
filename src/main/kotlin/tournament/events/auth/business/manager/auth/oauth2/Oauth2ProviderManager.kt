package tournament.events.auth.business.manager.auth.oauth2

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus.BAD_REQUEST
import io.micronaut.http.uri.UriBuilder
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.business.exception.businessExceptionOf
import tournament.events.auth.business.manager.ProviderUserInfoManager
import tournament.events.auth.business.manager.UserManager
import tournament.events.auth.business.manager.auth.AuthManager
import tournament.events.auth.business.manager.auth.AuthorizeStateManager
import tournament.events.auth.business.model.provider.EnabledProvider
import tournament.events.auth.business.model.provider.Provider
import tournament.events.auth.business.model.provider.config.ProviderOauth2
import tournament.events.auth.business.model.oauth2.AuthorizationRequest
import tournament.events.auth.business.model.oauth2.ProviderOauth2Tokens
import tournament.events.auth.business.model.oauth2.State
import tournament.events.auth.business.model.oauth2.TokenRequest
import tournament.events.auth.business.model.provider.ProviderCredentials
import tournament.events.auth.client.oauth2.TokenEndpointClient
import tournament.events.auth.util.loggerForClass
import java.net.URI

@Singleton
class Oauth2ProviderManager(
    @Inject private val authManager: AuthManager,
    @Inject private val stateManager: AuthorizeStateManager,
    @Inject private val tokenEndpointClient: TokenEndpointClient,
    @Inject private val userManager: UserManager,
    @Inject private val userDetailsManager: ProviderUserInfoManager,
) {

    private val logger = loggerForClass()

    fun getOauth2(provider: EnabledProvider): ProviderOauth2 {
        if (provider.auth !is ProviderOauth2) {
            throw businessExceptionOf(BAD_REQUEST, "exception.provider.oauth2.unsupported")
        }
        return provider.auth
    }

    /**
     * Return the redirect uri that must be called by the third-party OAuth 2 provider after the user
     * has authenticated in an authorization code grant flow.
     */
    fun getRedirectUri(provider: Provider): URI {
        return UriBuilder.of(authManager.getRedirectUri())
            .path("providers")
            .path(provider.id)
            .path("callback")
            .build()
    }

    suspend fun authorizeWithProvider(
        provider: EnabledProvider,
        state: State
    ): HttpResponse<*> {
        val oauth2 = getOauth2(provider)
        val redirectUri = AuthorizationRequest(
            oauth2 = oauth2,
            responseType = "code",
            redirectUri = getRedirectUri(provider),
            state = stateManager.encodeState(state)
        ).build()
        return HttpResponse.redirect<Any>(redirectUri)
    }

    suspend fun getAuthenticationWithAuthorizationCodeOrError(
        provider: EnabledProvider,
        authorizeCode: String?,
        error: String?,
        errorDescription: String?
    ): ProviderCredentials {
        val oauth2 = getOauth2(provider)

        return if (authorizeCode != null) {
            fetchTokens(provider, oauth2, authorizeCode)
        } else {
            TODO() // Handle error
        }
    }

    suspend fun fetchTokens(
        provider: Provider,
        oauth2: ProviderOauth2,
        authorizeCode: String
    ): ProviderOauth2Tokens {
        val request = TokenRequest(
            oauth2 = oauth2,
            authorizeCode = authorizeCode,
            redirectUri = getRedirectUri(provider)
        )
        val tokens = tokenEndpointClient.fetchTokens(request)
        return ProviderOauth2Tokens(
            accessToken = tokens.accessToken,
            refreshToken = tokens.refreshToken
        )
    }
}
