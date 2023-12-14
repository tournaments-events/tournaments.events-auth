package tournament.events.auth.view

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS
import jakarta.inject.Inject
import tournament.events.auth.business.manager.provider.ProviderUserInfoManager
import tournament.events.auth.business.manager.UserManager
import tournament.events.auth.business.manager.auth.AuthorizeStateManager
import tournament.events.auth.business.manager.provider.ProviderConfigManager
import tournament.events.auth.business.manager.provider.ProviderManager
import tournament.events.auth.business.manager.auth.oauth2.Oauth2ProviderManager

@Controller("/providers/{id}")
class ProviderController(
    @Inject private val authorizeStateManager: AuthorizeStateManager,
    @Inject private val oauth2ProviderManager: Oauth2ProviderManager,
    @Inject private val providerConfigManager: ProviderConfigManager,
    @Inject private val providerManager: ProviderManager,
    @Inject private val userManager: UserManager,
    @Inject private val userDetailsManager: ProviderUserInfoManager,
) {

    @Get("authorize")
    @Secured(IS_ANONYMOUS)
    suspend fun authorize(
        id: String,
        @QueryValue("state") serializedState: String?
    ): HttpResponse<*> {
        val state = authorizeStateManager.verifyEncodedState(serializedState)
        val provider = providerConfigManager.findEnabledProviderById(id)
        return providerManager.authorizeWithProvider(provider, state)
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
        val state = authorizeStateManager.verifyEncodedState(serializedState)
        val authentication = oauth2ProviderManager.getAuthenticationWithAuthorizationCodeOrError(
            provider = provider,
            authorizeCode = code,
            error = error,
            errorDescription = errorDescription
        )

        val userDetails = userDetailsManager.fetchUserInfo(provider, authentication)

        val existingUser = userManager.findByProviderUserId(
            providerId = userDetails.providerId,
            userId = userDetails.userId
        )
        val user = if (existingUser == null) {
            userManager.createOrAssociateUserWithUserDetails(userDetails)
        } else {
            userManager.refreshUserDetails(existingUser, userDetails)
        }
        return TODO()
    }
}
