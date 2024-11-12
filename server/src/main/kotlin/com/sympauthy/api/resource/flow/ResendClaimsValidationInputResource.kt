package com.sympauthy.api.resource.flow

import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    description = "Information about the validation code to resend to the end-user."
)
data class ResendClaimsValidationInputResource(
    @get:Schema(
        description = "The media to send the validation code to."
    )
    val media: String
)
