package tournament.events.auth.business.manager.user

import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.business.model.user.claim.Claim
import tournament.events.auth.business.model.user.claim.StandardClaim
import tournament.events.auth.config.model.ClaimsConfig
import tournament.events.auth.config.model.orThrow

@Singleton
class ClaimManager(
    @Inject private val uncheckedClaimsConfig: ClaimsConfig
) {

    private val cachedClaimsMap by lazy {
        uncheckedClaimsConfig.orThrow().claims
            .associateBy { it.id }
    }

    /**
     * Return all the [StandardClaim] enabled on this authorization server.
     */
    fun listStandardClaims(): List<StandardClaim> {
        TODO()
    }

    /**
     * Return the [Claim] identified by [id] or null.
     */
    fun findById(id: String): Claim? {
        return cachedClaimsMap[id]
    }


}
