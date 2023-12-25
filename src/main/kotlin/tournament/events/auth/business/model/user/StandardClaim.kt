package tournament.events.auth.business.model.user

/**
 * List of standard claims that are defined in the OpenID specification.
 *
 * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims">Standard claims</a>
 */
enum class StandardClaim {
    SUBJECT,

    NAME,
    GIVEN_NAME,
    FAMILY_NAME,
    MIDDLE_NAME,
    NICKNAME,

    PREFERRED_USERNAME,
    PROFILE,
    PICTURE,
    WEBSITE,

    EMAIL,
    EMAIL_VERIFIED,

    GENDER,
    BIRTH_DATE,

    ZONE_INFO,
    LOCALE,

    PHONE_NUMBER,
    PHONE_NUMBER_VERIFIED,

    UPDATED_AT
}
