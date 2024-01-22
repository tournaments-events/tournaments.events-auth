package com.sympauthy.api.resource.oauth2

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable
import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    name = "TokenResource",
    description = "The access token and (optionally) refresh token issue by this authorization server.",
    externalDocs = ExternalDocumentation(
        url = "https://datatracker.ietf.org/doc/html/rfc6749#section-5.1"
    )
)
@Serdeable
data class TokenResource(
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("token_type") val tokenType: String,
    @JsonProperty("expired_in") val expiredIn: Int?,
    @JsonProperty("scope") val scope: String? = null,
    @JsonProperty("refresh_token") val refreshToken: String? = null,
    @JsonProperty("id_token") val idToken: String? = null
)
