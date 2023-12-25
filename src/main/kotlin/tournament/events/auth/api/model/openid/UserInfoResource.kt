package tournament.events.auth.api.model.openid

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

@Serdeable
data class UserInfoResource(
    @Schema(
        description = "Identifier for the end-user.",
    )
    val sub: String,
    @Schema(
        description = "End-user's full name."
    )
    val name: String?,
    @Schema(
        description = "Given name(s) or first name(s) of the end-user."
    )
    @JsonProperty("given_name")
    val givenName: String?,
    @Schema(
        description = "Surname(s) or last name(s) of the end-user."
    )
    @JsonProperty("family_name")
    val familyName: String?,
    @Schema(
        description = "Middle name(s) of the end-user."
    )
    @JsonProperty("middle_name")
    val middleName: String?,
    @Schema(
        description = "Casual name of the end-user."
    )
    val nickname: String?,
    @Schema(
        description = "Shorthand name of the end-user."
    )
    @JsonProperty("preferred_username")
    val preferredUsername: String?,
    @Schema(
        description = "URL of the end-user's profile page."
    )
    val profile: String?,
    @Schema(
        description = "URL of the end-user's profile picture."
    )
    val picture: String?,
    @Schema(
        description = "URL of the end-user's Web page or blog."
    )
    val website: String?,
    @Schema(
        description = "End-user's preferred e-mail address."
    )
    val email: String?,
    @Schema(
        description = "True if the end-user's e-mail address has been verified, otherwise false."
    )
    @JsonProperty("email_verified")
    val emailVerified: String?,
    @Schema(
        description = "End-user's gender."
    )
    val gender: String?,
    @Schema(
        description = "End-user's birthday."
    )
    val birthDate: LocalDate?,
    @Schema(
        description = "String from IANA Time Zone Database representing the end-user's time zone."
    )
    @JsonProperty("zoneinfo")
    val zoneInfo: LocalDate?,
    @Schema(
        description = "End-user's locale, represented as a BCP47 language tag."
    )
    val locale: String?,
    @Schema(
        description = "End-user's preferred telephone number."
    )
    @JsonProperty("phone_number")
    val phoneNumber: String?,
    @Schema(
        description = "True if the end-user's phone number has been verified, otherwise false."
    )
    @JsonProperty("phone_number_verified")
    val phoneNumberVerified: Boolean?,
    // TODO: address
    @Schema(
        description = "Time the end-user's information was last updated. It is the number of seconds from epoch in UTC timezone."
    )
    val updatedAt: LocalDateTime?
)
