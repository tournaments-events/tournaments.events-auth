package com.sympauthy.api.resource.flow

import io.micronaut.serde.annotation.Serdeable
import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    description = """
List of required validation codes send to the user by this authorization server to validate some claims populated during
the authorization flow.
"""
)
@Serdeable
data class ClaimsValidationResource(
    val codes: List<ValidationCodeResource>
)
