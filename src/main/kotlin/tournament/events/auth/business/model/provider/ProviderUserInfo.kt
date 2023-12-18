package tournament.events.auth.business.model.provider

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID


data class ProviderUserInfo(
    /**
     * Identifier of the provider providing those user information.
     */
    val providerId: String,
    /**
     * Identifier of the user.
     */
    val userId: UUID,
    /**
     * Last time this application fetched the info from the provider.
     */
    val lastFetchDate: LocalDateTime,
    /**
     * Last time this application detected a change of the info returned by the provider.
     */
    val lastChangeDate: LocalDateTime,
    val userInfo: RawProviderUserInfo
)

/**
 * Information about the user provided from a third-party authentication provider.
 *
 * The list of information we can obtain of a third-party provider is based on the list of information
 * exposed by the [OpenId user info endpoint](https://openid.net/specs/openid-connect-core-1_0.html#UserInfo).
 */
data class RawProviderUserInfo(
    /**
     * The identifier of the user for the third-party provider.
     *
     * This is the only user info required in order to associate
     */
    val subject: String,

    val name: String? = null,
    val givenName: String? = null,
    val familyName: String? = null,
    val middleName: String? = null,
    val nickname: String? = null,

    val preferredUsername: String? = null,
    val profile: String? = null,
    val picture: String? = null,
    val website: String? = null,

    val email: String? = null,
    val emailVerified: Boolean? = null,

    val gender: String? = null,
    val birthDate: LocalDate? = null,

    val zoneInfo: String? = null,
    val locale: String? = null,

    val phoneNumber: String? = null,
    val phoneNumberVerified: Boolean? = null,

    // TODO address
    /**
     * Last time the user information where updated. (UTC)
     */
    val updatedAt: LocalDateTime? = null
)
