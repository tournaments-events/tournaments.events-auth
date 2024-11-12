package com.sympauthy.api.resource.flow

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable
import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    description = """
Result indicating if the user is expected to enter a code to validates some claims collected 
or if the user can continue the authorization flow.
    """
)
@Serdeable
data class SendValidationCodeOrGetFlowResultResource(

    @get:Schema(
        name = "validation_code",
        description = "Information about the validation code sent to the user by the authorization server.",
    )
    @get:JsonProperty("code")
    val code: ValidationCodeResource? = null,

    @get:Schema(
        name = "redirect_url",
        description = """
URL where the end-user must be redirected to continue the authentication flow.
The URL is present only if there is no more validation code are required from the user.

The end-user will either:
- continue the authentication flow.
- be redirected to the client if the authentication flow is completed.
        """
    )
    @get:JsonProperty("redirect_url")
    val redirectUrl: String? = null
)
