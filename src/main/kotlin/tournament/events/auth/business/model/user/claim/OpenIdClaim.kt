package tournament.events.auth.business.model.user.claim

import tournament.events.auth.business.model.user.StandardScope

/**
 * Enumeration of claims, defined in the OpenID specification, that are supported by this application.
 *
 * TODO: Add standard address claims
 * TODO: Support custom claims
 *
 * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims">Standard claims</a>
 */
enum class OpenIdClaim(
    val id: String,
    val dataType: ClaimDataType,
    /**
     * Scope that must be requested by the client to disclose the claim through the user endpoint.
     */
    val scope: StandardScope
) {
    SUBJECT(
        Id.SUB,
        ClaimDataType.STRING,
        StandardScope.PROFILE
    ),
    NAME(
        Id.NAME,
        ClaimDataType.STRING,
        StandardScope.PROFILE
    ),
    GIVEN_NAME(
        Id.GIVEN_NAME,
        ClaimDataType.STRING,
        StandardScope.PROFILE
    ),
    FAMILY_NAME(
        Id.FAMILY_NAME,
        ClaimDataType.STRING,
        StandardScope.PROFILE
    ),
    MIDDLE_NAME(
        Id.MIDDLE_NAME,
        ClaimDataType.STRING,
        StandardScope.PROFILE
    ),
    NICKNAME(
        Id.NICKNAME,
        ClaimDataType.STRING,
        StandardScope.PROFILE
    ),
    PREFERRED_USERNAME(
        Id.PREFERRED_USERNAME,
        ClaimDataType.STRING,
        StandardScope.PROFILE
    ),
    PROFILE(
        Id.PROFILE,
        ClaimDataType.STRING,
        StandardScope.PROFILE
    ),
    PICTURE(
        Id.PICTURE,
        ClaimDataType.STRING,
        StandardScope.PROFILE
    ),
    WEBSITE(
        Id.WEBSITE,
        ClaimDataType.STRING,
        StandardScope.PROFILE
    ),
    EMAIL(
        Id.EMAIL,
        ClaimDataType.STRING,
        StandardScope.EMAIL
    ),
    EMAIL_VERIFIED(
        Id.EMAIL_VERIFIED,
        ClaimDataType.STRING,
        StandardScope.EMAIL
    ),
    GENDER(
        Id.GENDER,
        ClaimDataType.STRING,
        StandardScope.PROFILE
    ),
    BIRTH_DATE(
        Id.BIRTH_DATE,
        ClaimDataType.STRING,
        StandardScope.PROFILE
    ),
    ZONE_INFO(
        Id.ZONE_INFO,
        ClaimDataType.STRING,
        StandardScope.PROFILE
    ),
    LOCALE(
        Id.LOCALE,
        ClaimDataType.STRING,
        StandardScope.PROFILE
    ),
    PHONE_NUMBER(
        Id.PHONE_NUMBER,
        ClaimDataType.STRING,
        StandardScope.PHONE
    ),
    PHONE_NUMBER_VERIFIED(
        Id.PHONE_NUMBER_VERIFIED,
        ClaimDataType.STRING,
        StandardScope.PHONE
    ),
    UPDATED_AT(
        Id.UPDATED_AT,
        ClaimDataType.STRING,
        StandardScope.PROFILE
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
