package com.sympauthy.api.resource.flow

import com.fasterxml.jackson.annotation.JsonAnySetter
import io.micronaut.serde.annotation.Serdeable

@Serdeable
class ClaimInputResource {
    @set:JsonAnySetter
    var claims: Map<String, Any> = emptyMap()
}
