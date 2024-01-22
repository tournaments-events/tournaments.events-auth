package com.sympauthy.api.controller.flow

import com.sympauthy.api.resource.flow.PasswordOptionsResource
import com.sympauthy.api.resource.flow.SignInConfigurationResource
import com.sympauthy.api.resource.flow.SignUpInputResource
import com.sympauthy.business.manager.provider.ProviderConfigManager
import com.sympauthy.security.SecurityRule.HAS_VALID_STATE
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.swagger.v3.oas.annotations.Operation
import jakarta.inject.Inject

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
