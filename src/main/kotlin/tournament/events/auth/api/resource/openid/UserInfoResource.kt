package tournament.events.auth.api.resource.openid

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable
import io.swagger.v3.oas.annotations.media.Schema
import tournament.events.auth.business.model.user.StandardClaimId.BIRTH_DATE
import tournament.events.auth.business.model.user.StandardClaimId.EMAIL
import tournament.events.auth.business.model.user.StandardClaimId.EMAIL_VERIFIED
import tournament.events.auth.business.model.user.StandardClaimId.FAMILY_NAME
import tournament.events.auth.business.model.user.StandardClaimId.GENDER
import tournament.events.auth.business.model.user.StandardClaimId.GIVEN_NAME
import tournament.events.auth.business.model.user.StandardClaimId.LOCALE
import tournament.events.auth.business.model.user.StandardClaimId.MIDDLE_NAME
import tournament.events.auth.business.model.user.StandardClaimId.NICKNAME
import tournament.events.auth.business.model.user.StandardClaimId.PHONE_NUMBER
import tournament.events.auth.business.model.user.StandardClaimId.PHONE_NUMBER_VERIFIED
import tournament.events.auth.business.model.user.StandardClaimId.PICTURE
import tournament.events.auth.business.model.user.StandardClaimId.PREFERRED_USERNAME
import tournament.events.auth.business.model.user.StandardClaimId.PROFILE
import tournament.events.auth.business.model.user.StandardClaimId.SUB
import tournament.events.auth.business.model.user.StandardClaimId.UPDATED_AT
import tournament.events.auth.business.model.user.StandardClaimId.WEBSITE
import tournament.events.auth.business.model.user.StandardClaimId.ZONE_INFO
import java.time.LocalDate

@Serdeable
data class UserInfoResource(
    @get:Schema(
        description = "Identifier for the end-user.",
    )
    @JsonProperty(SUB)
    val sub: String,
    @get:Schema(
        description = "End-user's full name."
    )
    val name: String?,
    @get:Schema(
        description = "Given name(s) or first name(s) of the end-user."
    )
    @JsonProperty(GIVEN_NAME)
    val givenName: String?,
    @get:Schema(
        description = "Surname(s) or last name(s) of the end-user."
    )
    @JsonProperty(FAMILY_NAME)
    val familyName: String?,
    @get:Schema(
        description = "Middle name(s) of the end-user."
    )
    @JsonProperty(MIDDLE_NAME)
    val middleName: String?,
    @get:Schema(
        description = "Casual name of the end-user."
    )
    @JsonProperty(NICKNAME)
    val nickname: String?,
    @get:Schema(
        description = "Shorthand name of the end-user."
    )
    @JsonProperty(PREFERRED_USERNAME)
    val preferredUsername: String?,
    @get:Schema(
        description = "URL of the end-user's profile page."
    )
    @JsonProperty(PROFILE)
    val profile: String?,
    @get:Schema(
        description = "URL of the end-user's profile picture."
    )
    @JsonProperty(PICTURE)
    val picture: String?,
    @get:Schema(
        description = "URL of the end-user's Web page or blog."
    )
    @JsonProperty(WEBSITE)
    val website: String?,
    @get:Schema(
        description = "End-user's preferred e-mail address."
    )
    @JsonProperty(EMAIL)
    val email: String?,
    @get:Schema(
        description = "True if the end-user's e-mail address has been verified, otherwise false."
    )
    @JsonProperty(EMAIL_VERIFIED)
    val emailVerified: String?,
    @get:Schema(
        description = "End-user's gender."
    )
    @JsonProperty(GENDER)
    val gender: String?,
    @get:Schema(
        description = "End-user's birthday."
    )
    @JsonProperty(BIRTH_DATE)
    val birthDate: LocalDate?,
    @get:Schema(
        description = "String from IANA Time Zone Database representing the end-user's time zone."
    )
    @JsonProperty(ZONE_INFO)
    val zoneInfo: LocalDate?,
    @get:Schema(
        description = "End-user's locale, represented as a BCP47 language tag."
    )
    @JsonProperty(LOCALE)
    val locale: String?,
    @get:Schema(
        description = "End-user's preferred telephone number."
    )
    @JsonProperty(PHONE_NUMBER)
    val phoneNumber: String?,
    @get:Schema(
        description = "True if the end-user's phone number has been verified, otherwise false."
    )
    @JsonProperty(PHONE_NUMBER_VERIFIED)
    val phoneNumberVerified: Boolean?,
    // TODO: address
    @get:Schema(
        description = "Time the end-user's information was last updated. It is the number of seconds from epoch in UTC timezone."
    )
    @JsonProperty(UPDATED_AT)
    val updatedAt: Long?
)
