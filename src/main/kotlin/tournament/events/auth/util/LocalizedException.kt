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
     * Identifier of the message detailing the issue in a technical way.
     *
     * It is mostly meant for developers and administrators to troubleshoot the issue.
     *
     * This is also used as an identifier for the error reported to the user.
     */
    val detailsId: String,
    /**
     * Identifier of the message displayed to the end user.
     *
     * This message is meant to be displayed to the end user. It must help the user to understand what went wrong and how
     * to get out of the situation if possible.
     */
    val descriptionId: String? = null,
    /**
     * Value to expose to the mustache template to inject values into the localized message.
     */
    val values: Map<String, Any?> = emptyMap(),
    val additionalMessages: List<AdditionalLocalizedMessage> = emptyList(),
    /**
     * Underlying cause of this exception.
     */
    throwable: Throwable? = null
) : Exception(formatMessage(status, detailsId, values), throwable) {
    companion object {
        private fun formatMessage(status: HttpStatus, messageId: String, values: Map<String, Any?>): String {
            return if (values.isEmpty()) {
                "${status.code} - $messageId"
            } else {
                "${status.code} - $messageId: $values"
            }
        }
    }
}

data class AdditionalLocalizedMessage(
    val path: String,
    val messageId: String,
    val values: Map<String, Any> = emptyMap()
)
