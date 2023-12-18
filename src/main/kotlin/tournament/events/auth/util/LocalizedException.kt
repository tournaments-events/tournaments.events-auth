package tournament.events.auth.util

import io.micronaut.http.HttpStatus

/**
 * Base class for exceptions where the message is localized in a resource bundle.
 */
open class LocalizedException(
    /**
     * HTTP status to respond
     */
    val status: HttpStatus,
    /**
     * Resource name of the message to print to the end-user.
     */
    val messageResourceName: String,
    /**
     * Value to expose to the mustache template to inject values into the localized message.
     */
    val values: Map<String, Any?>,
    val additionalMessages: List<AdditionalLocalizedMessage> = emptyList(),
    /**
     * Underlying cause of this exception.
     */
    throwable: Throwable?
) : Exception(formatMessage(status, messageResourceName, values), throwable) {
    companion object {
        private fun formatMessage(status: HttpStatus, messageResourceName: String, values: Map<String, Any?>): String {
            return if (values.isEmpty()) {
                "${status.code} - $messageResourceName"
            } else {
                "${status.code} - $messageResourceName: $values"
            }
        }
    }
}

data class AdditionalLocalizedMessage(
    val path: String,
    val messageResourceName: String,
    val values: Map<String, Any> = emptyMap()
)
