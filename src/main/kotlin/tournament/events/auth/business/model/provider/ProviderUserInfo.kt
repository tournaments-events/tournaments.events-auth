package tournament.events.auth.business.model.provider

import tournament.events.auth.business.model.user.RawUserInfo
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
    val fetchDate: LocalDateTime,
    /**
     * Last time this application detected a change of the info returned by the provider.
     */
    val changeDate: LocalDateTime,
    // FIXME: val idToken: String Store the id token we have used to extract those info.
    val userInfo: RawUserInfo
)
