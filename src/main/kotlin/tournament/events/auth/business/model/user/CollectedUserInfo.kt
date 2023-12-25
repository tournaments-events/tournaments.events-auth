package tournament.events.auth.business.model.user

import java.util.UUID

/**
 * Information that we collected from the user as a first party.
 */
class CollectedUserInfo(
    val userId: UUID,
    /**
     * List of info that we tried to collect.
     *
     * We keep a list separated from the information to remember which information we tried to collect
     * but the user denied to answer.
     */
    val collectedInfo: List<StandardClaim>,
    /**
     * Information that have been collected.
     */
    val info: RawUserInfo,
)
