package tournament.events.auth.api.resource.flow

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable
import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    description = """
For ALL URLs contained in this configuration, the state query param must be added by the client before using any
of those urls.
    """
)
@Serdeable
data class ConfigurationResource(
    @JsonProperty("features")
    val features: FeaturesResource,
    @JsonProperty("password")
    val password: PasswordConfigurationResource?,
    @get:Schema(
        description = "List of configuration of third-party providers."
    )
    @JsonProperty("providers")
    val providers: List<ProviderConfigurationResource>
)

@Schema(
    description = "List of boolean flags indicating which features are enabled on this authentication server."
)
@Serdeable
data class FeaturesResource(
    @get:Schema(
        description = "Authentication of the end-user using a login and a password couple."
    )
    @JsonProperty("password-sign-in")
    val passwordSignIn: Boolean,
    @get:Schema(
        description = "End-user account creation."
    )
    @JsonProperty("sign-up")
    val signUp: Boolean,
)

@Schema(
    description = "Configuration related to a third-party provider that can be used by the end-user to authenticate."
)
@Serdeable
data class ProviderConfigurationResource(
    @get:Schema(
        description = "Identifier of the third-party provider."
    )
    @JsonProperty("id")
    val id: String,
    @get:Schema(
        description = "Name of the third-party provider as it should be displayed to the end-user."
    )
    @JsonProperty("name")
    val name: String,
    @get:Schema(
        description = """
URL to redirect the end-user to to initiate a authorization grant flow with the third-party provider.
        """
    )
    @JsonProperty("authorize_url")
    val authorizeUrl: String
)

@Schema(
    description = """
If null or not present, the authentication by password is disabled by the authentication server.
        """
)
@Serdeable
data class PasswordConfigurationResource(
    @get:Schema(
        description = "List of claims that the end-user can use to sign-in."
    )
    @JsonProperty("login-claims")
    val loginClaims: List<String>
)
