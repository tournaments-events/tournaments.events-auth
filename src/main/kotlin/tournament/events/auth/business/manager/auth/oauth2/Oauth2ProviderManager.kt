package tournament.events.auth.business.manager.auth.oauth2

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.HttpStatus.BAD_REQUEST
import io.micronaut.transaction.annotation.Transactional
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.api.controller.flow.ProvidersController.Companion.FLOW_PROVIDER_CALLBACK_ENDPOINT
import tournament.events.auth.api.controller.flow.ProvidersController.Companion.FLOW_PROVIDER_ENDPOINTS
import tournament.events.auth.business.exception.businessExceptionOf
import tournament.events.auth.business.manager.auth.AuthManager
import tournament.events.auth.business.manager.provider.ProviderUserInfoManager
import tournament.events.auth.business.manager.user.CollectedUserInfoManager
import tournament.events.auth.business.manager.user.CreateOrAssociateResult
import tournament.events.auth.business.manager.user.UserManager
import tournament.events.auth.business.model.oauth2.AuthorizeAttempt
import tournament.events.auth.business.model.provider.EnabledProvider
import tournament.events.auth.business.model.provider.Provider
import tournament.events.auth.business.model.provider.config.ProviderOauth2Config
import tournament.events.auth.business.model.provider.oauth2.ProviderOAuth2TokenRequest
import tournament.events.auth.business.model.provider.oauth2.ProviderOauth2Tokens
import tournament.events.auth.business.model.redirect.AuthorizeRedirect
import tournament.events.auth.business.model.redirect.ProviderOauth2AuthorizationRedirect
import tournament.events.auth.business.model.user.CollectedUserInfoUpdate
import tournament.events.auth.business.model.user.RawUserInfo
import tournament.events.auth.business.model.user.User
import tournament.events.auth.business.model.user.UserMergingStrategy
import tournament.events.auth.business.model.user.claim.OpenIdClaim
import tournament.events.auth.business.model.user.claim.StandardClaim
import tournament.events.auth.business.security.AdminContext
import tournament.events.auth.client.oauth2.TokenEndpointClient
import tournament.events.auth.config.model.AdvancedConfig
import tournament.events.auth.config.model.UrlsConfig
import tournament.events.auth.config.model.getUri
import tournament.events.auth.config.model.orThrow
import tournament.events.auth.exception.httpExceptionOf
import java.net.URI
import java.util.*

@Singleton
open class Oauth2ProviderManager(
    @Inject private val authManager: AuthManager,
    @Inject private val authorizationCodeManager: AuthorizationCodeManager,
    @Inject private val authorizeManager: AuthorizeManager,
    @Inject private val collectedUserInfoManager: CollectedUserInfoManager,
    @Inject private val userManager: UserManager,
    @Inject private val providerUserInfoManager: ProviderUserInfoManager,
    @Inject private val stateManager: AuthorizeManager,
    @Inject private val tokenEndpointClient: TokenEndpointClient,
    @Inject private val uncheckedAdvancedConfig: AdvancedConfig,
    @Inject private val uncheckedUrlsConfig: UrlsConfig
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
        return uncheckedUrlsConfig.orThrow().getUri(
            FLOW_PROVIDER_ENDPOINTS + FLOW_PROVIDER_CALLBACK_ENDPOINT,
            "providerId" to provider.id
        )
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
        return HttpResponse.temporaryRedirect<Any>(redirectUri)
    }

    suspend fun handleCallback(
        provider: EnabledProvider,
        authorizeCode: String,
        state: String?
    ): AuthorizeRedirect {
        val authorizeAttempt = authorizeManager.verifyEncodedState(state)

        val oauth2 = getOauth2(provider)
        val authentication = fetchTokens(provider, oauth2, authorizeCode)

        val rawUserInfo = providerUserInfoManager.fetchUserInfo(provider, authentication)

        val existingUserInfo = providerUserInfoManager.findByProviderAndSubject(
            provider = provider,
            subject = rawUserInfo.subject
        )

        val userId = if (existingUserInfo == null) {
            createOrAssociateUserWithProviderUserInfo(provider, rawUserInfo).user.id
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

    /**
     * Create a new [User] or associate to an existing [User].
     * Then update the provider user info with the newly collected [providerUserInfo].
     *
     * Depending on the ```advanced.user-merging-strategy```, we may instead associate the [providerUserInfo] to
     * an existing user.
     */
    @Transactional
    open suspend fun createOrAssociateUserWithProviderUserInfo(
        provider: EnabledProvider,
        providerUserInfo: RawUserInfo
    ): CreateOrAssociateResult {
        return when (uncheckedAdvancedConfig.orThrow().userMergingStrategy) {
            UserMergingStrategy.BY_MAIL -> createOrAssociateUserByEmailWithProviderUserInfo(provider, providerUserInfo)
            UserMergingStrategy.NONE -> createUserWithProviderUserInfo(provider, providerUserInfo)
        }
    }

    /**
     * Create a new [User] or associate it to a [User] that have the same email.
     * and update the user info collected by the [provider] with the [providerUserInfo].
     *
     * If the ```advanced.user-merging-strategy``` is set to ```by-mail```, we will check if we have an existing user
     * with the email first. If yes, we will only update the user info, otherwise, we will create it.
     *
     * The email is collected and copied as a first party data. We want this information to be stable
     * and not be affected by changes from the third party in the future.
     * Otherwise, an update from a provider may break our uniqueness and cause uncontrolled side effects.
     */
    @Transactional
    internal open suspend fun createOrAssociateUserByEmailWithProviderUserInfo(
        provider: EnabledProvider,
        providerUserInfo: RawUserInfo
    ): CreateOrAssociateResult {
        val email = providerUserInfo.email ?: throw httpExceptionOf(
            HttpStatus.INTERNAL_SERVER_ERROR, "user.create_with_provider.missing_email",
            "providerId" to provider.id
        )
        val existingUser = userManager.findByEmail(email)
        val user = existingUser
            ?: userManager.createUser().apply {
                collectedUserInfoManager.updateUserInfo(
                    // We use the admin context as the email may not be part of the claims requested by the client
                    // but is necessary for the merging strategy to work.
                    context = AdminContext,
                    user = this,
                    updates = listOf(
                        CollectedUserInfoUpdate(
                            claim = StandardClaim(OpenIdClaim.EMAIL),
                            value = Optional.of(email)
                        )
                    )
                )
            }

        providerUserInfoManager.saveUserInfo(
            provider = provider,
            userId = user.id,
            rawUserInfo = providerUserInfo
        )
        return CreateOrAssociateResult(
            created = existingUser == null,
            user = user
        )
    }

    @Transactional
    internal open suspend fun createUserWithProviderUserInfo(
        provider: EnabledProvider,
        providerUserInfo: RawUserInfo
    ): CreateOrAssociateResult {
        TODO("FIXME")
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
