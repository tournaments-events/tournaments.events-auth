package com.sympauthy.business.model.user

import java.time.LocalDate
import java.util.*

/**
 * Contains updates to apply to user info.
 *
 * For each field:
 * - ```null``` means the field has not been updated.
 * - ```Optional.empty()``` means the field has been updated to null by the end-user.
 */
data class RawUserInfoUpdate(
    val name: Optional<String>? = null,
    val givenName: Optional<String>? = null,
    val familyName: Optional<String>? = null,
    val middleName: Optional<String>? = null,
    val nickname: Optional<String>? = null,

    val preferredUsername: Optional<String>? = null,
    val profile: Optional<String>? = null,
    val picture: Optional<String>? = null,
    val website: Optional<String>? = null,

    val email: Optional<String>? = null,
    val emailVerified: Optional<Boolean>? = null,

    val gender: Optional<String>? = null,
    val birthDate: Optional<LocalDate>? = null,

    val zoneInfo: Optional<String>? = null,
    val locale: Optional<String>? = null,

    val phoneNumber: Optional<String>? = null,
    val phoneNumberVerified: Optional<Boolean>? = null
)
