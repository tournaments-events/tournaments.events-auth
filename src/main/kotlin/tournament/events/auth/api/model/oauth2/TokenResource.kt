package tournament.events.auth.api.model.oauth2

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class TokenResource(
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("token_type") val tokenType: String,
    @JsonProperty("expired_in") val expiredIn: Int,
    @JsonProperty("scope") val scope: String,
    @JsonProperty("refresh_token") val refreshToken: String?,
    @JsonProperty("id_token") val idToken: String?
)
