package com.sympauthy.api.resource.flow

import io.micronaut.serde.annotation.Serdeable
import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    description = "A validation code send to the user by this authorization server."
)
@Serdeable
data class ValidationCodeResource(
    @get:Schema(
        description = "The media used to send the code to the user.",
        example = "MAIL"
    )
    val media: String,
    @get:Schema(
        description = """
The list of reasons this code was send to the user.
- ```EMAIL_CLAIM```: Verify if the user has access to the email box he is pretending to own.
- ```RESET_PASSWORD```: Verify the user has access to the email box associated to an existing account before 
  allowing him to reset its password.
"""
    )
    val reasons: List<String>
)
