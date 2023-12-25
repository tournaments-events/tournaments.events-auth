package tournament.events.auth.business.manager.auth.oauth2

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus.BAD_REQUEST
import io.micronaut.http.uri.UriBuilder
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.business.exception.businessExceptionOf
import tournament.events.auth.business.manager.provider.ProviderUserInfoManager
import tournament.events.auth.business.manager.UserManager
import tournament.events.auth.business.manager.auth.AuthManager
import tournament.events.auth.business.model.oauth2.AuthorizeAttempt
import tournament.events.auth.business.model.provider.EnabledProvider
import tournament.events.auth.business.model.provider.Provider
import tournament.events.auth.business.model.provider.config.ProviderOauth2Config
import tournament.events.auth.business.model.redirect.ProviderOauth2AuthorizationRedirect
import tournament.events.auth.business.model.provider.oauth2.ProviderOauth2Tokens
import tournament.events.auth.business.model.provider.oauth2.ProviderOAuth2TokenRequest
import tournament.events.auth.business.model.provider.ProviderCredentials
import tournament.events.auth.business.model.redirect.AuthorizeRedirect
import tournament.events.auth.client.oauth2.TokenEndpointClient
import java.net.URI

@Singleton
class Oauth2ProviderManager(
    @Inject private val authManager: AuthManager,
    @Inject private val authorizationCodeManager: AuthorizationCodeManager,
    @Inject private val authorizeManager: AuthorizeManager,
    @Inject private val providerUserInfoManager: ProviderUserInfoManager,
    @Inject private val stateManager: AuthorizeManager,
    @Inject private val tokenEndpointClient: TokenEndpointClient,
    @Inject private val userManager: UserManager,
    @Inject private val userDetailsManager: ProviderUserInfoManager,
) {

    fun getOauth2(provider: EnabledProvider): ProviderOauth2Config {
        if (provider.auth !is ProviderOauth2Config) {
            throw businessExceptionOf(BAD_REQUEST, "provider.oauth2.unsupported")
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
        authorizeAttempt: AuthorizeAttempt
    ): HttpResponse<*> {
        val oauth2 = getOauth2(provider)
        val redirectUri = ProviderOauth2AuthorizationRedirect(
            oauth2 = oauth2,
            responseType = "code",
            redirectUri = getRedirectUri(provider),
            state = stateManager.encodeState(authorizeAttempt)
        ).build()
        return HttpResponse.redirect<Any>(redirectUri)
    }

    suspend fun handleCallback(
        provider: EnabledProvider,
        authorizeCode: String?,
        error: String?,
        errorDescription: String?,
        state: String?
    ): AuthorizeRedirect {
        val authorizeAttempt = authorizeManager.verifyEncodedState(state)

        val authentication = getAuthenticationWithAuthorizationCodeOrError(
            provider = provider,
            authorizeCode = authorizeCode,
            error = error,
            errorDescription = errorDescription
        )

        val rawUserInfo = userDetailsManager.fetchUserInfo(provider, authentication)

        val existingUserInfo = providerUserInfoManager.findByProviderAndSubject(
            provider = provider,
            subject = rawUserInfo.subject
        )

        val userId = if (existingUserInfo == null) {
            userManager.createOrAssociateUserWithUserInfo(provider, rawUserInfo)
                .user.id
        } else {
            providerUserInfoManager.refreshUserInfo(existingUserInfo, rawUserInfo)
            existingUserInfo.userId
        }
        authorizeManager.setAuthenticatedUserId(authorizeAttempt, userId)

        // TODO: add redirection to complete user info.
        // TODO: add redirection to verify email.

        return AuthorizeRedirect(
            authorizeAttempt = authorizeAttempt,
            authorizationCode = authorizationCodeManager.generateCode(authorizeAttempt)
        )
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
        oauth2: ProviderOauth2Config,
        authorizeCode: String
    ): ProviderOauth2Tokens {
        val request = ProviderOAuth2TokenRequest(
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
