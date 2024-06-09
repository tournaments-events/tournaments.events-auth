package com.sympauthy.business.model

import java.time.LocalDateTime

/**
 * An object that will expire.
 */
interface Expirable {
    val expirationDate: LocalDateTime

    val expired: Boolean
        get() = expirationDate.isBefore(LocalDateTime.now())
}

/**
 * An object that may expire.
 */
interface MaybeExpirable {
    val expirationDate: LocalDateTime?

    val expired: Boolean
        get() = expirationDate?.isBefore(LocalDateTime.now()) == true
}
