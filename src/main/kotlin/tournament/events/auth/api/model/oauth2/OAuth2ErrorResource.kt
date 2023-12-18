package tournament.events.auth.api.model.oauth2

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class OAuth2ErrorResource(
    @JsonProperty("error_code") val errorCode: String,
    val description: String? = null,
)
