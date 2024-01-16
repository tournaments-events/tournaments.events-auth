package tournament.events.auth.api.resource.flow

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class SignUpInputResource(
    @JsonProperty("claims")
    val claims: Map<String, Any>,
    @JsonProperty("password")
    val password: String
)
