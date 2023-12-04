package tournament.events.auth.business.exception

import io.micronaut.http.HttpStatus
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
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
    messageId: String,
    values: Map<String, Any> = emptyMap(),
    additionalMessages: List<AdditionalLocalizedMessage> = emptyList(),
    throwable: Throwable? = null
) : LocalizedException(status, messageId, values, additionalMessages, throwable)

fun businessExceptionOf(
    status: HttpStatus,
    messageId: String,
    vararg values: Pair<String, Any>
): BusinessException = BusinessException(status, messageId, mapOf(*values))

inline fun <reified T : Any> singleBusinessExceptionOf(
    status: HttpStatus,
    messageId: String,
    vararg values: Pair<String, Any>
): Single<T> = Single.error(BusinessException(status, messageId, mapOf(*values)))
