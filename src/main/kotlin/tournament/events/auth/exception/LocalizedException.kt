package tournament.events.auth.exception

/**
 * Base class for exceptions where the message is localized in a resource bundle.
 */
open class LocalizedException(
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
    val throwable: Throwable? = null
) : Exception(formatMessage(detailsId, values), throwable) {
    companion object {
        private fun formatMessage(messageId: String, values: Map<String, Any?>): String {
            return if (values.isEmpty()) {
                messageId
            } else {
                "$messageId: $values"
            }
        }
    }
}

data class AdditionalLocalizedMessage(
    val path: String,
    val messageId: String,
    val values: Map<String, Any> = emptyMap()
)

fun localizedExceptionOf(
    detailsId: String,
    vararg values: Pair<String, Any?>
) = LocalizedException(
    detailsId = detailsId,
    values = mapOf(*values)
)
