package com.sympauthy.business.exception

import com.sympauthy.exception.AdditionalLocalizedMessage
import com.sympauthy.exception.LocalizedException
import io.micronaut.http.HttpStatus

/**
 * Exception describing an error that will be exposed to the end-user.
 * Ex. if the user provided an identifier that does not exist in the system.
 *
 * The description must be localized and comprehensible by the user to allow him to solve the issue.
 */
class BusinessException(
    detailsId: String,
    descriptionId: String? = null,
    values: Map<String, Any?> = emptyMap(),
    val recommendedStatus: HttpStatus? = null,
    additionalMessages: List<AdditionalLocalizedMessage> = emptyList(),
    throwable: Throwable? = null
) : LocalizedException(detailsId, descriptionId, values, additionalMessages, throwable)

fun businessExceptionOf(
    detailsId: String,
    descriptionId: String? = null,
    recommendedStatus: HttpStatus? = null,
    vararg values: Pair<String, Any?>
): BusinessException = BusinessException(
    detailsId = detailsId,
    descriptionId = descriptionId,
    recommendedStatus = recommendedStatus,
    values = mapOf(*values)
)
