package com.sympauthy.api.resource

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class VersionResource(
    @get:JsonProperty("api_versions")
    val apiVersions: List<String>
)
