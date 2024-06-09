package com.sympauthy.api.resource.flow

import io.micronaut.serde.annotation.Serdeable
import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    description = "A validation code send to the user by this authorization server."
)
@Serdeable
data class ValidationCodeResource(
    @get:Schema(
        description = "The media used to send the code to the user."
    )
    val media: ValidationCodeMediaResource,
    @get:Schema(
        description = "The list of reasons this code was send to the user."
    )
    val reasons: List<ValidationCodeReasonResource>
)


@Schema(
    description = """
Enumeration of media this authorization server can use to send validation code to the user:
- ```EMAIL```
"""
)
enum class ValidationCodeMediaResource {
    EMAIL
}

@Schema(
    description = """
Enumeration of reason this authorization server send validation code to the user:
- ```EMAIL_CLAIM```: Verify if the user has access to the email box he is pretending to own.
- ```RESET_PASSWORD```: Verify the user has access to the email box associated to an existing account before 
  allowing him to reset its password.
"""
)
enum class ValidationCodeReasonResource {
    EMAIL_CLAIM,
    RESET_PASSWORD
}
