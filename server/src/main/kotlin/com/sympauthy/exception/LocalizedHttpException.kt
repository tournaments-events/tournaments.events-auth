package com.sympauthy.exception

import io.micronaut.http.HttpStatus

/**
 * Base class for exceptions that will cause this server to return
 */
open class LocalizedHttpException(
    val status: HttpStatus,
    detailsId: String,
    descriptionId: String? = null,
    values: Map<String, Any?> = emptyMap(),
    additionalMessages: List<AdditionalLocalizedMessage> = emptyList(),
    throwable: Throwable? = null
): LocalizedException(
    detailsId = detailsId,
    descriptionId = descriptionId,
    values = values,
    additionalMessages = additionalMessages,
    throwable = throwable
)

fun <T: LocalizedException> T.toHttpException(httpStatus: HttpStatus) = LocalizedHttpException(
    status = httpStatus,
    detailsId = detailsId,
    descriptionId = descriptionId,
    values = values,
    additionalMessages = additionalMessages,
    throwable = throwable
)

fun httpExceptionOf(
    status: HttpStatus,
    detailsId: String,
    vararg values: Pair<String, Any?>
): LocalizedHttpException = LocalizedHttpException(
    status = status,
    detailsId = detailsId,
    values = mapOf(*values)
)

fun httpExceptionOf(
    status: HttpStatus,
    detailsId: String,
    descriptionId: String?,
    vararg values: Pair<String, Any?>
): LocalizedHttpException = LocalizedHttpException(
    status = status,
    detailsId = detailsId,
    descriptionId = descriptionId,
    values = mapOf(*values)
)

fun httpExceptionOf(
    status: HttpStatus,
    detailsId: String,
    descriptionId: String?,
    throwable: Throwable?,
    vararg values: Pair<String, Any?>
): LocalizedHttpException = LocalizedHttpException(
    status = status,
    detailsId = detailsId,
    descriptionId = descriptionId,
    values = mapOf(*values),
    throwable = throwable
)
