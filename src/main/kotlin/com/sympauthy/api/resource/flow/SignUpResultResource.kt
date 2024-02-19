package com.sympauthy.api.resource.flow

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable
import io.swagger.v3.oas.annotations.media.Schema

@Serdeable
class SignUpResultResource(
    @get:Schema(
        description = """
URL where the end-user must be redirected.

The end-user will either:
- continue the sign-up.
- be redirected to the client if the account creation is completed.
        """
    )
    @JsonProperty("redirect_url")
    val redirectUrl: String
)
