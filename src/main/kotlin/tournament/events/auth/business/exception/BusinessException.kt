package tournament.events.auth.business.exception

import io.micronaut.http.HttpStatus
import io.micronaut.http.HttpStatus.INTERNAL_SERVER_ERROR
import tournament.events.auth.util.AdditionalLocalizedMessage
import tournament.events.auth.util.LocalizedException

/**
 * Exception describing an error that will be exposed to the end-user.
 * Ex. if the user provided an identifier that does not exist in the system.
 *
 * The description must be localized and comprehensible by the user to allow him to solve the issue.
 */
class BusinessException(
    status: HttpStatus,
    detailsId: String,
    userMessageId: String? = null,
    values: Map<String, Any?> = emptyMap(),
    additionalMessages: List<AdditionalLocalizedMessage> = emptyList(),
    throwable: Throwable? = null
) : LocalizedException(status, detailsId, userMessageId, values, additionalMessages, throwable)

fun businessExceptionOf(
    status: HttpStatus,
    technicalMessageId: String,
    vararg values: Pair<String, Any?>
): BusinessException = BusinessException(
    status = status,
    detailsId = technicalMessageId,
    values = mapOf(*values)
)

fun businessExceptionOf(
    status: HttpStatus,
    technicalMessageId: String,
    userMessageId: String?,
    vararg values: Pair<String, Any?>
): BusinessException = BusinessException(
    status = status,
    detailsId = technicalMessageId,
    userMessageId = userMessageId,
    values = mapOf(*values)
)

inline fun <reified T : Any> T?.orMissingConfig(key: String): T {
    if (this == null) {
        throw businessExceptionOf(
            INTERNAL_SERVER_ERROR,
            "exception.config.missing_key",
            "key" to key
        )
    }
    return this
}
