package com.sympauthy.api.resource.flow

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable

@Serdeable
class SignUpInputResource(
    @JsonProperty("password")
    val password: String
) {
    @set:JsonAnySetter
    var claims: Map<String, Any> = emptyMap()
}
