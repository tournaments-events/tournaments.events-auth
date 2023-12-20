package tournament.events.auth.api.model.error

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable
import io.swagger.v3.oas.annotations.media.Schema

@Serdeable
data class ErrorResource(
    @get:Schema(description = "HTTP status responded.")
    val status: Int,

    @JsonProperty("error_code")
    @get:Schema(description = "A code identifying the error.")
    val errorCode: String,

    @JsonProperty("description")
    @get:Schema(description = "A message explaining the error to the end-user. It may contain information on how to recover from the issue.")
    val description: String?,

    @JsonProperty("details")
    @get:Schema(description = "A message containing technical details about the error.")
    val details: String?
)
