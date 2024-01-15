package tournament.events.auth.api.controller.flow

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS
import io.swagger.v3.oas.annotations.Operation
import jakarta.inject.Inject
import tournament.events.auth.api.controller.flow.ProvidersController.Companion.FLOW_PROVIDER_AUTHORIZE_ENDPOINT
import tournament.events.auth.api.controller.flow.ProvidersController.Companion.FLOW_PROVIDER_ENDPOINTS
import tournament.events.auth.api.resource.flow.ConfigurationResource
import tournament.events.auth.api.resource.flow.FeaturesResource
import tournament.events.auth.api.resource.flow.ProviderConfigurationResource
import tournament.events.auth.business.manager.provider.ProviderConfigManager
import tournament.events.auth.business.model.provider.EnabledProvider
import tournament.events.auth.config.model.*

@Secured(IS_ANONYMOUS)
@Controller("/api/v1/flow/configuration")
class ConfigurationController(
    @Inject private val providerManager: ProviderConfigManager,
    @Inject private val uncheckedUrlsConfig: UrlsConfig,
    @Inject private val uncheckedPasswordAuthConfig: PasswordAuthConfig,
) {

    @Operation(
        description = """
Expose the configuration for end-user authentication flow.

The configuration contains: 
- which means the user can use to authenticate (with password, which third-party provider).
- which information are collected during authentication.
        """,
        tags = ["flow"]
    )
    @Get
    suspend fun getConfiguration(): ConfigurationResource {
        val urlsConfig = uncheckedUrlsConfig.orThrow()
        val passwordAuthConfig = uncheckedPasswordAuthConfig.orThrow()

        val features = getFeatures(
            passwordAuthConfig = passwordAuthConfig
        )
        val providers = providerManager.listEnabledProviders()
            .map { getProvider(it, urlsConfig) }

        return ConfigurationResource(
            features = features,
            password = null,
            providers = providers
        )
    }

    private fun getProvider(
        provider: EnabledProvider,
        urlsConfig: EnabledUrlsConfig
    ): ProviderConfigurationResource {
        val authorizeUrl = urlsConfig.getUri(
            FLOW_PROVIDER_ENDPOINTS + FLOW_PROVIDER_AUTHORIZE_ENDPOINT,
            "providerId" to provider.id
        )
        return ProviderConfigurationResource(
            id = provider.id,
            name = provider.name,
            authorizeUrl = authorizeUrl.toString()
        )
    }

    private fun getFeatures(
        passwordAuthConfig: EnabledPasswordAuthConfig
    ): FeaturesResource {
        return FeaturesResource(
            passwordSignIn = passwordAuthConfig.enabled,
            signUp = false // FIXME
        )
    }
}
