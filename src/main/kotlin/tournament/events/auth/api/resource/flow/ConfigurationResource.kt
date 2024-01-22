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
    @Schema(
        description = "List of claims that may be collected by the client."
    )
    @JsonProperty("claims")
    val claims: List<ClaimResource>,
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
    description = "Configuration related to a claim that can be collected from the end-user."
)
data class ClaimResource(
    @get:Schema(
        description = "Identifier of the claim."
    )
    val id: String,
    @get:Schema(
        description = "Localized name of the claim."
    )
    val name: String,
    @get:Schema(
        description = """
Type of the value accepted for the claim. 

Supported values are:
- ```string```
- ```number```
- ```date```
        """
    )
    val type: String
)

@Schema(
    description = "List of boolean flags indicating which features are enabled on this authorization server."
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
If null or not present, the authentication by password is disabled by the authorization server.
        """
)
@Serdeable
data class PasswordConfigurationResource(
    @get:Schema(
        description = "List of claims that the end-user can use to sign-in."
    )
    @JsonProperty("login-claims")
    val loginClaims: List<String>,
    @get:Schema(
        description = """
List of claims the end-user MUST provide in addition to its password to sign-up.
"""
    )
    @JsonProperty("sign-up-claims")
    val signUpClaims: List<CollectedClaimConfigurationResource>,
)

@Schema(
    description = "Configuration of a claim collected from the end-user at a step of the flow."
)
@Serdeable
data class CollectedClaimConfigurationResource(
    @Schema(description = "Identifier of the claim.")
    val id: String,
    @Schema(description =
    """
Order in which the claim must be presented to the end-user. Lowest value must be presented first.
    """)
    @JsonProperty("index")
    val index: Int,
    @Schema(description = "True if a value must be collected before the end-user can continue the flow.")
    val required: Boolean
)
