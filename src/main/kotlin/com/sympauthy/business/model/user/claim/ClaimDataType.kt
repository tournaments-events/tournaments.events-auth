package com.sympauthy.business.model.user.claim

import kotlin.reflect.KClass

/**
 * Enumeration of supported data type for an end-user claim.
 */
enum class ClaimDataType(
    val typeClass: KClass<*>
) {
    STRING(String::class),
    EMAIL(String::class),
    PHONE_NUMBER(String::class)
}
