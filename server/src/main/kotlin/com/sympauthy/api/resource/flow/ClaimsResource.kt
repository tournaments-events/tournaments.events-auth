package com.sympauthy.api.resource.flow

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable
import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    description = """
Collectable claims collected by the authorization server for the end-user signing-in/up in the authorization flow.
"""
)
@Serdeable
data class ClaimsResource(
    @get:Schema(
        description = "List of claims."
    )
    val claims: List<ClaimValueResource>
)

@Schema(
    description = "A claim and the value the authorization server has collected for it."
)
@Serdeable
data class ClaimValueResource(
    @get:Schema(
        description = "The claim."
    )
    val claim: String,
    @get:Schema(
        description = """
True if a value for this claim has been collected by the authorization server as a first-party.
        """
    )
    val collected: Boolean,
    @get:Schema(
        description = """
A value for the claim that the authorization server has collected as a first-party (through API or authorization flow).

If this value is missing and collected is true, it means the authorization server has already asked the end-user 
about this claims but the end-user declined to fill the claim (ex. by leaving it empty during the authentication flow).
        """
    )
    val value: Any?,
    @get:Schema(
        description = """
A value for the claim that the authorization server has collected from an external provider.  
        """
    )
    @get:JsonProperty("suggested_value")
    val suggestedValue: Any? = null
)
