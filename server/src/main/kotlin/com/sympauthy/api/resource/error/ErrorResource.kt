package com.sympauthy.api.resource.error

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable
import io.swagger.v3.oas.annotations.media.Schema

@Serdeable
data class ErrorResource(
    @get:Schema(description = "The HTTP status of the response.")
    val status: Int,

    @get:Schema(description = "A code identifying the error.")
    @get:JsonProperty("error_code")
    val errorCode: String,

    @get:Schema(description = "A message explaining the error to the end-user. It may contain information on how to recover from the issue.")
    val description: String?,

    @get:Schema(description = "A message containing technical details about the error.")
    val details: String?,

    @get:Schema(description = "List of invalid properties causing the error and details about the reason.")
    val properties: List<PropertyErrorResource>?
)

@Schema(
    description = """
Details about an error caused by a property.

It is mostly used by this authorization server to add details on validation error. 
It contains the path to the invalid property and the reason why the property is invalid (missing, wrong type, etc.). 
"""
)
@Serdeable
data class PropertyErrorResource(
    @get:Schema(description = "Path to one of the property of the payload causing the error.")
    val path: String,

    @get:Schema(description = """
A human-readable message explaining the error to the end-user.
It is intended to be displayed to the end-user, in order for him to correct its input and retry the operation.
""")
    val description: String?
)
