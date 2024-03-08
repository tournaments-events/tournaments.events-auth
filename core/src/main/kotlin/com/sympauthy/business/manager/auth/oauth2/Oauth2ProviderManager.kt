package com.sympauthy.business.manager.auth.oauth2

import com.sympauthy.api.controller.flow.ProvidersController.Companion.FLOW_PROVIDER_CALLBACK_ENDPOINT
import com.sympauthy.api.controller.flow.ProvidersController.Companion.FLOW_PROVIDER_ENDPOINTS
import com.sympauthy.api.util.AuthorizeRedirect
import com.sympauthy.business.exception.businessExceptionOf
import com.sympauthy.business.manager.ClaimManager
import com.sympauthy.business.manager.provider.ProviderClaimsManager
import com.sympauthy.business.manager.user.CollectedClaimManager
import com.sympauthy.business.manager.user.CreateOrAssociateResult
import com.sympauthy.business.manager.user.UserManager
import com.sympauthy.business.model.oauth2.AuthorizeAttempt
import com.sympauthy.business.model.provider.EnabledProvider
import com.sympauthy.business.model.provider.Provider
import com.sympauthy.business.model.provider.config.ProviderOauth2Config
import com.sympauthy.business.model.provider.oauth2.ProviderOAuth2TokenRequest
import com.sympauthy.business.model.provider.oauth2.ProviderOauth2Tokens
import com.sympauthy.business.model.redirect.ProviderOauth2AuthorizationRedirect
import com.sympauthy.business.model.user.CollectedClaimUpdate
import com.sympauthy.business.model.user.RawProviderClaims
import com.sympauthy.business.model.user.UserMergingStrategy.BY_MAIL
import com.sympauthy.business.model.user.UserMergingStrategy.NONE
import com.sympauthy.business.model.user.claim.OpenIdClaim.Id.EMAIL
import com.sympauthy.business.security.AdminContext
import com.sympauthy.client.oauth2.TokenEndpointClient
import com.sympauthy.config.model.AdvancedConfig
import com.sympauthy.config.model.UrlsConfig
import com.sympauthy.config.model.getUri
import com.sympauthy.config.model.orThrow
import com.sympauthy.exception.httpExceptionOf
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus.BAD_REQUEST
import io.micronaut.http.HttpStatus.INTERNAL_SERVER_ERROR
import io.micronaut.transaction.annotation.Transactional
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.net.URI
import java.util.*

@Singleton
open class Oauth2ProviderManager(
    @Inject private val authorizationCodeManager: AuthorizationCodeManager,
    @Inject private val authorizeManager: AuthorizeManager,
    @Inject private val claimManager: ClaimManager,
    @Inject private val collectedClaimManager: CollectedClaimManager,
    @Inject private val userManager: UserManager,
    @Inject private val providerClaimsManager: ProviderClaimsManager,
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

        val rawUserInfo = providerClaimsManager.fetchUserInfo(provider, authentication)

        val existingUserInfo = providerClaimsManager.findByProviderAndSubject(
            provider = provider,
            subject = rawUserInfo.subject
        )

        val userId = if (existingUserInfo == null) {
            createOrAssociateUserWithProviderUserInfo(provider, rawUserInfo).user.id
        } else {
            providerClaimsManager.refreshUserInfo(existingUserInfo, rawUserInfo)
            existingUserInfo.userId
        }
        authorizeManager.setAuthenticatedUserId(authorizeAttempt, userId)

        // TODO: call
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
        providerUserInfo: RawProviderClaims
    ): CreateOrAssociateResult {
        return when (uncheckedAdvancedConfig.orThrow().userMergingStrategy) {
            BY_MAIL -> createOrAssociateUserByEmailWithProviderUserInfo(provider, providerUserInfo)
            NONE -> createUserWithProviderUserInfo(provider, providerUserInfo)
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
        providerUserInfo: RawProviderClaims
    ): CreateOrAssociateResult {
        val email = providerUserInfo.email ?: throw httpExceptionOf(
            INTERNAL_SERVER_ERROR, "user.create_with_provider.missing_email",
            "providerId" to provider.id
        )
        val emailClaim = claimManager.findById(EMAIL) ?: throw httpExceptionOf(
            INTERNAL_SERVER_ERROR, "user.create_with_provider.missing_email_claim"
        )
        val existingUser = userManager.findByEmail(email)
        val user = existingUser
            ?: userManager.createUser().apply {
                collectedClaimManager.updateUserInfo(
                    // We use the admin context as the email may not be part of the claims requested by the client
                    // but is necessary for the merging strategy to work.
                    context = AdminContext,
                    user = this,
                    updates = listOf(
                        CollectedClaimUpdate(
                            claim = emailClaim,
                            value = Optional.of(email)
                        )
                    )
                )
            }

        providerClaimsManager.saveUserInfo(
            provider = provider,
            userId = user.id,
            rawProviderClaims = providerUserInfo
        )
        return CreateOrAssociateResult(
            created = existingUser == null,
            user = user
        )
    }

    @Transactional
    internal open suspend fun createUserWithProviderUserInfo(
        provider: EnabledProvider,
        providerUserInfo: RawProviderClaims
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
