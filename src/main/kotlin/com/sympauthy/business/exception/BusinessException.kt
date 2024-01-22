package com.sympauthy.business.exception

import com.sympauthy.exception.AdditionalLocalizedMessage
import com.sympauthy.exception.LocalizedHttpException
import io.micronaut.http.HttpStatus

/**
 * Exception describing an error that will be exposed to the end-user.
 * Ex. if the user provided an identifier that does not exist in the system.
 *
 * The description must be localized and comprehensible by the user to allow him to solve the issue.
 */
class BusinessException(
    status: HttpStatus,
    detailsId: String,
    descriptionId: String? = null,
    values: Map<String, Any?> = emptyMap(),
    additionalMessages: List<AdditionalLocalizedMessage> = emptyList(),
    throwable: Throwable? = null
) : LocalizedHttpException(status, detailsId, descriptionId, values, additionalMessages, throwable)

fun businessExceptionOf(
    status: HttpStatus,
    detailsId: String,
    vararg values: Pair<String, Any?>
): BusinessException = BusinessException(
    status = status,
    detailsId = detailsId,
    values = mapOf(*values)
)

fun businessExceptionOf(
    status: HttpStatus,
    detailsId: String,
    descriptionId: String?,
    vararg values: Pair<String, Any?>
): BusinessException = BusinessException(
    status = status,
    detailsId = detailsId,
    descriptionId = descriptionId,
    values = mapOf(*values)
)
