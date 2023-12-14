package tournament.events.auth.config.model

enum class UserInfoConfigEnum(val configName: String) {
    SUBJECT("sub"),

    NAME("name"),
    GIVEN_NAME("given_name"),
    FAMILY_NAME("family_name"),
    MIDDLE_NAME("middle_name"),
    NICKNAME("middle_name"),

    PREFERRED_USERNAME("preferred_username"),
    PROFILE("preferred_username"),
    PICTURE("picture"),
    WEBSITE("website"),

    EMAIL("email"),
    EMAIL_VERIFIED("email_verified"),

    GENDER("gender"),
    BIRTH_DATE("birth_date"),

    ZONE_INFO("zone_info"),
    LOCALE("locale"),

    PHONE_NUMBER("phone_number"),
    PHONE_NUMBER_VERIFIED("phone_number_verified"),

    UPDATED_AT("updated_at")
}
