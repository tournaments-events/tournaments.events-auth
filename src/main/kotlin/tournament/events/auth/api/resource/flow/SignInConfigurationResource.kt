package tournament.events.auth.api.resource.flow

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

data class SignInConfigurationResource(
    @get:Schema(
        description = """
Options for password authentication.
If missing or null, the password authentication has been disabled on this authorization server.
"""
    )
    val password: PasswordOptionsResource?,
    @get:Schema(
        description = """
List of third-party providers that can be used by the end-user to authenticate.
"""
    )
    val providers: List<SignInProviderResource>?
)

data class PasswordOptionsResource(
    @JsonProperty("sign_up_url")
    val signUpUrl: String?,
    @JsonProperty("forgotten_password_url")
    val forgottenPasswordUrl: String?
)

data class SignInProviderResource(
    val name: String,
    val id: String
)
