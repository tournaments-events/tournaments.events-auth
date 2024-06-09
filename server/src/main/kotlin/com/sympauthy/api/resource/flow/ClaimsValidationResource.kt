package com.sympauthy.api.resource.flow

import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    description = """
List of required validation codes send to the user by this authorization server to validate some claims populated during
the authorization flow.
"""
)
data class ClaimsValidationResource(
    val codes: List<ValidationCodeResource>
)
