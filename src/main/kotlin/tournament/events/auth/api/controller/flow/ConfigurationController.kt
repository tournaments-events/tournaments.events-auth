package tournament.events.auth.api.controller.flow

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS
import io.swagger.v3.oas.annotations.Operation
import jakarta.inject.Inject
import tournament.events.auth.api.resource.flow.ConfigurationResource
import tournament.events.auth.api.resource.flow.FeaturesResource
import tournament.events.auth.api.resource.flow.ProviderConfigurationResource
import tournament.events.auth.business.manager.provider.ProviderConfigManager
import tournament.events.auth.business.model.provider.EnabledProvider
import tournament.events.auth.config.model.EnabledPasswordAuthConfig
import tournament.events.auth.config.model.PasswordAuthConfig
import tournament.events.auth.config.model.orThrow

@Secured(IS_ANONYMOUS)
@Controller("/api/flow/1.0/configuration")
class ConfigurationController(
    @Inject private val providerManager: ProviderConfigManager,
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
        val passwordAuthConfig = uncheckedPasswordAuthConfig.orThrow()

        val features = getFeatures(
            passwordAuthConfig = passwordAuthConfig
        )
        val providers = providerManager.listEnabledProviders().map(this::getProvider)

        return ConfigurationResource(
            features = features,
            password = null,
            providers = providers
        )
    }

    private fun getProvider(provider: EnabledProvider): ProviderConfigurationResource {
        return ProviderConfigurationResource(
            id = provider.id,
            name = provider.name,
            authorizeUrl = TODO()
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
