package tournament.events.auth.api.controller.flow

import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.swagger.v3.oas.annotations.Operation
import jakarta.inject.Inject
import tournament.events.auth.api.resource.flow.PasswordOptionsResource
import tournament.events.auth.api.resource.flow.SignInConfigurationResource
import tournament.events.auth.api.resource.flow.SignUpInputResource
import tournament.events.auth.business.manager.provider.ProviderConfigManager
import tournament.events.auth.security.SecurityRule.HAS_VALID_STATE

// http://localhost:8092/api/oauth2/authorize?response_type=code&client_id=example&redirect_uri=http://example.com&state=whatever

@Secured(HAS_VALID_STATE)
@Controller("/api/v1/flow/sign-up")
class SignUpController(
    @Inject private val providerConfigManager: ProviderConfigManager
) {

    @Operation(
        description = """
Initiate the creation of an account of a end-user with a password.
        """,
        tags = ["flow"]
    )
    suspend fun signUp(
        @Body inputResource: SignUpInputResource
    ) {

    }

    @Operation(
        description = """
Expose the configuration for end-user authentication flow.
        """,
        tags = ["flow"]
    )
    @Get
    suspend fun getSignInOptions(): SignInConfigurationResource {
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
