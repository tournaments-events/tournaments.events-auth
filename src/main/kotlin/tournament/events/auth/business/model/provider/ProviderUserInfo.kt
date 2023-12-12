package tournament.events.auth.business.model.provider

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Information about the user provider from a third-party authentication provider.
 *
 * The list of information we can obtain of a third-party provider is based on the list of information
 * exposed by the [OpenId user info endpoint](https://openid.net/specs/openid-connect-core-1_0.html#UserInfo).
 */
data class ProviderUserInfo(
    /**
     * Identifier of the provider providing those user information.
     */
    val providerId: String,
    /**
     * Identifier of the user.
     */
    val userId: String,

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
    val lastUpdateDate: LocalDateTime? = null
)
