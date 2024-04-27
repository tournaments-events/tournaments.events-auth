package com.sympauthy.api.resource.error

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable
import io.swagger.v3.oas.annotations.media.Schema

@Serdeable
data class ErrorResource(
    @get:Schema(description = "HTTP status responded.")
    val status: Int,

    @get:Schema(description = "A code identifying the error.")
    @get:JsonProperty("error_code")
    val errorCode: String,

    @get:Schema(description = "A message explaining the error to the end-user. It may contain information on how to recover from the issue.")
    @get:JsonProperty("description")
    val description: String?,

    @get:Schema(description = "A message containing technical details about the error.")
    @get:JsonProperty("details")
    val details: String?
)
