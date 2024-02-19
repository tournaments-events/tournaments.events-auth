package com.sympauthy.business.model.user.claim

import com.sympauthy.business.model.user.StandardScope
import com.sympauthy.business.model.user.claim.ClaimDataType.STRING
import com.sympauthy.business.model.user.claim.ClaimGroup.IDENTITY

/**
 * Enumeration of claims, defined in the OpenID specification, that are supported by this application.
 *
 * TODO: Add standard address claims
 *
 * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims">Standard claims</a>
 */
enum class OpenIdClaim(
    val id: String,
    val type: ClaimDataType,
    /**
     * Group the claim is part of.
     * Claims sharing the same [group] are related one to another.
     * Ex. first name & last name.
     */
    val group: ClaimGroup? = null,
    /**
     * Scope that must be requested by the client to disclose the claim through the user endpoint.
     */
    val scope: StandardScope
) {
    SUBJECT(
        id = Id.SUB,
        type = STRING,
        scope = StandardScope.PROFILE
    ),
    NAME(
        id = Id.NAME,
        type = STRING,
        group = IDENTITY,
        scope = StandardScope.PROFILE
    ),
    GIVEN_NAME(
        id = Id.GIVEN_NAME,
        type = STRING,
        group = IDENTITY,
        scope = StandardScope.PROFILE
    ),
    FAMILY_NAME(
        id = Id.FAMILY_NAME,
        type = STRING,
        group = IDENTITY,
        scope = StandardScope.PROFILE
    ),
    MIDDLE_NAME(
        id = Id.MIDDLE_NAME,
        type = STRING,
        group = IDENTITY,
        scope = StandardScope.PROFILE
    ),
    NICKNAME(
        id = Id.NICKNAME,
        type = STRING,
        scope = StandardScope.PROFILE
    ),
    PREFERRED_USERNAME(
        id = Id.PREFERRED_USERNAME,
        type = STRING,
        scope = StandardScope.PROFILE
    ),
    PROFILE(
        id = Id.PROFILE,
        type = STRING,
        scope = StandardScope.PROFILE
    ),
    PICTURE(
        id = Id.PICTURE,
        type = STRING,
        scope = StandardScope.PROFILE
    ),
    WEBSITE(
        id = Id.WEBSITE,
        type = STRING,
        scope = StandardScope.PROFILE
    ),
    EMAIL(
        id = Id.EMAIL,
        type = ClaimDataType.EMAIL,
        scope = StandardScope.EMAIL
    ),
    EMAIL_VERIFIED(
        Id.EMAIL_VERIFIED,
        type = STRING,
        scope = StandardScope.EMAIL
    ),
    GENDER(
        id = Id.GENDER,
        type = STRING,
        scope = StandardScope.PROFILE
    ),
    BIRTH_DATE(
        id = Id.BIRTH_DATE,
        type = STRING,
        scope = StandardScope.PROFILE
    ),
    ZONE_INFO(
        id = Id.ZONE_INFO,
        type = STRING,
        scope = StandardScope.PROFILE
    ),
    LOCALE(
        id = Id.LOCALE,
        type = STRING,
        scope = StandardScope.PROFILE
    ),
    PHONE_NUMBER(
        id = Id.PHONE_NUMBER,
        type = STRING,
        scope = StandardScope.PHONE
    ),
    PHONE_NUMBER_VERIFIED(
        id = Id.PHONE_NUMBER_VERIFIED,
        type = STRING,
        scope = StandardScope.PHONE
    ),
    UPDATED_AT(
        id = Id.UPDATED_AT,
        type = STRING,
        scope = StandardScope.PROFILE
    );

    object Id {
        const val SUB = "sub"
        const val NAME = "name"
        const val GIVEN_NAME = "given_name"
        const val FAMILY_NAME = "family_name"
        const val MIDDLE_NAME = "middle_name"
        const val NICKNAME = "nickname"
        const val PREFERRED_USERNAME = "preferred_username"
        const val PROFILE = "profile"
        const val PICTURE = "picture"
        const val WEBSITE = "website"
        const val EMAIL = "email"
        const val EMAIL_VERIFIED = "email_verified"
        const val GENDER = "gender"
        const val BIRTH_DATE = "birth_date"
        const val ZONE_INFO = "zoneinfo"
        const val LOCALE = "locale"
        const val PHONE_NUMBER = "phone_number"
        const val PHONE_NUMBER_VERIFIED = "phone_number_verified"
        const val UPDATED_AT = "updated_at"
    }
}
