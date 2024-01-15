package tournament.events.auth.business.model.user

import tournament.events.auth.business.model.user.claim.Claim
import java.time.LocalDateTime
import java.util.*

/**
 * Information that we collected from the user as a first party.
 */
data class CollectedUserInfo(
    val userId: UUID,
    val claim: Claim,
    /**
     * The value of the claim.
     * It may be null if the end-user has deliberately deleted the user info.
     */
    val value: Any?,
    /**
     * Whether the value of this claim has been verified by this authorization server or by the client.
     * null if the verification is not relevant for the claim.
     */
    val verified: Boolean?,
    val collectionDate: LocalDateTime
)
