package tournament.events.auth.api.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

data class ErrorResource(
    @get:Schema(description = "HTTP status responded.")
    val status: Int,

    @get:Schema(description = "A code identifying the error.")
    val code: String,

    @get:Schema(description = " A message explaining the reason of the error.")
    val message: String?,

    @JsonProperty("additional_messages")
    @get:Schema(description = """
Additional messages providing more details if the error is caused by one or more property.
""")
    val additionalMessages: List<AdditionalMessageResource>?
)

data class AdditionalMessageResource(
    @get:Schema(description = "Path of the property")
    val path: String,

    @get:Schema(description = "A code identifying the error associated to the propriety.")
    val code: String? = null,

    @get:Schema(description = "A message explaining the reason of the error.")
    val message: String? = null
)
