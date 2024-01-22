package com.sympauthy.api.resource.error

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class OAuth2ErrorResource(
    @JsonProperty("error_code")
    val errorCode: String,
    val details: String? = null,
    val description: String? = null,
)
