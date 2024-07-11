package com.sympauthy.api.resource.flow

import io.micronaut.serde.annotation.Serdeable
import io.swagger.v3.oas.annotations.media.Schema

@Serdeable
@Schema(
    description = "Result containing the new validation codes that have been sent to the user."
)
data class ResendClaimsValidationCodesResultResource(
    @get:Schema(
        name = "List of new codes send to the user requiring validation."
    )
    val codes: List<ValidationCodeResource>? = null,
)
