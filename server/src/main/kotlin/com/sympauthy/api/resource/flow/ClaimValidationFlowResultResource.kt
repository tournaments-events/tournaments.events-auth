package com.sympauthy.api.resource.flow

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    description = """
Response returned after a successful validation of a validation code submitted by the user.

It can either contains:
- the other validation code the user must sub
    """
)
data class ClaimValidationFlowResultResource(
    @get:Schema(
        name = "redirect_url",
        description = """
URL where the end-user must be redirected to continue the authentication flow.

The end-user will either:
- continue the authentication flow. ex. if the end-user email address is not validated.
- be redirected to the client if the authentication flow is completed.
        """
    )
    @get:JsonProperty("redirect_url")
    val redirectUrl: String
)
