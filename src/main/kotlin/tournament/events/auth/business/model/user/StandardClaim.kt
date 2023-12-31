package tournament.events.auth.business.model.user

/**
 * Enumeration of claims, defined in the OpenID specification, that are supported by this application.
 *
 * TODO: Add standard address claims
 * TODO: Support custom claims
 *
 * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims">Standard claims</a>
 */
enum class StandardClaim(
    val id: String,
    /**
     * Scope that must be requested by the client to disclose the claim through the user endpoint.
     */
    val scope: StandardScope
) {
    SUBJECT(
        StandardClaimId.SUB,
        StandardScope.PROFILE
    ),

    NAME(
        StandardClaimId.NAME,
        StandardScope.PROFILE
    ),
    GIVEN_NAME(
        StandardClaimId.GIVEN_NAME,
        StandardScope.PROFILE
    ),
    FAMILY_NAME(
        StandardClaimId.FAMILY_NAME,
        StandardScope.PROFILE
    ),
    MIDDLE_NAME(
        StandardClaimId.MIDDLE_NAME,
        StandardScope.PROFILE
    ),
    NICKNAME(
        StandardClaimId.NICKNAME,
        StandardScope.PROFILE
    ),

    PREFERRED_USERNAME(
        StandardClaimId.PREFERRED_USERNAME,
        StandardScope.PROFILE
    ),
    PROFILE(
        StandardClaimId.PROFILE,
        StandardScope.PROFILE
    ),
    PICTURE(
        StandardClaimId.PICTURE,
        StandardScope.PROFILE
    ),
    WEBSITE(
        StandardClaimId.WEBSITE,
        StandardScope.PROFILE
    ),

    EMAIL(
        StandardClaimId.EMAIL,
        StandardScope.EMAIL
    ),
    EMAIL_VERIFIED(
        StandardClaimId.EMAIL_VERIFIED,
        StandardScope.EMAIL
    ),

    GENDER(
        StandardClaimId.GENDER,
        StandardScope.PROFILE
    ),
    BIRTH_DATE(
        StandardClaimId.BIRTH_DATE,
        StandardScope.PROFILE
    ),

    ZONE_INFO(
        StandardClaimId.ZONE_INFO,
        StandardScope.PROFILE
    ),
    LOCALE(
        StandardClaimId.LOCALE,
        StandardScope.PROFILE
    ),

    PHONE_NUMBER(
        StandardClaimId.PHONE_NUMBER,
        StandardScope.PHONE
    ),
    PHONE_NUMBER_VERIFIED(
        StandardClaimId.PHONE_NUMBER_VERIFIED,
        StandardScope.PHONE
    ),

    UPDATED_AT(
        StandardClaimId.UPDATED_AT,
        StandardScope.PROFILE
    );
}

object StandardClaimId {
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
