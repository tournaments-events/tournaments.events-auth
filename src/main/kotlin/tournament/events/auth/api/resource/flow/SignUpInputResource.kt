package tournament.events.auth.api.resource.flow

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class SignUpInputResource(
    @JsonProperty("preferred_username")
    val preferredUsername: String?,
    @JsonProperty("email")
    val email: String?,
    @JsonProperty("password")
    val password: String
)
