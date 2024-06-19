package com.sympauthy.api.resource.flow

import io.micronaut.serde.annotation.Serdeable
import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    description = "A validation code inputted by the user requiring a validation from this authorization server."
)
@Serdeable
data class ClaimValidationInputResource(
    @get:Schema(
        description = "The media in which the user received the validation code."
    )
    val media: String,
    @get:Schema(
        description = "The validation code inputted by the user."
    )
    val code: String,
)
