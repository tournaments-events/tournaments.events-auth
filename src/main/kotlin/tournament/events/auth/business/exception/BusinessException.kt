package tournament.events.auth.business.exception

import io.micronaut.http.HttpStatus
import io.reactivex.rxjava3.core.Completable
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

inline fun <reified T> singleBusinessExceptionOf(
    status: HttpStatus,
    messageId: String,
    vararg values: Pair<String, Any>
): Single<T> = Single.error(BusinessException(status, messageId, mapOf(*values)))

inline fun <reified T> observableBusinessExceptionOf(
    status: HttpStatus,
    messageId: String,
    vararg values: Pair<String, Any>
): Observable<T> = Observable.error(BusinessException(status, messageId, mapOf(*values)))

fun completableBusinessExceptionOf(
    status: HttpStatus,
    messageId: String,
    vararg values: Pair<String, Any>
): Completable = Completable.error(BusinessException(status, messageId, mapOf(*values)))
