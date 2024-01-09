package tournament.events.auth.api.resource

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class VersionResource(
    @JsonProperty("api_versions")
    val apiVersions: List<String>
)
