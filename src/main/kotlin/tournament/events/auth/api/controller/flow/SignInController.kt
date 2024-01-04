package tournament.events.auth.api.controller.flow

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import jakarta.inject.Inject
import tournament.events.auth.api.resource.flow.PasswordOptionsResource
import tournament.events.auth.api.resource.flow.SignInConfigurationResource
import tournament.events.auth.business.manager.provider.ProviderConfigManager
import tournament.events.auth.server.security.SecurityRule.HAS_VALID_STATE

// http://localhost:8092/api/oauth2/authorize?response_type=code&client_id=example&redirect_uri=http://example.com&state=whatever

@Secured(HAS_VALID_STATE)
@Controller("/api/flow/1.0/sign-in")
class SignInController(
    @Inject private val providerConfigManager: ProviderConfigManager
) {

    @Get("/configuration")
    suspend fun getSignInConfiguration(): SignInConfigurationResource {
        val providers = providerConfigManager.listEnabledProviders()

        return SignInConfigurationResource(
            password = PasswordOptionsResource(
                signUpUrl = null,
                forgottenPasswordUrl = null
            ),
            providers = emptyList()
        )
    }
}
