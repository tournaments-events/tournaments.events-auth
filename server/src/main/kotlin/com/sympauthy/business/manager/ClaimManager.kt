package com.sympauthy.business.manager

import com.sympauthy.business.model.user.claim.Claim
import com.sympauthy.business.model.user.claim.StandardClaim
import com.sympauthy.config.model.ClaimsConfig
import com.sympauthy.config.model.orThrow
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class ClaimManager(
    @Inject private val uncheckedClaimsConfig: ClaimsConfig
) {

    private val cachedClaimsMap by lazy {
        uncheckedClaimsConfig.orThrow().claims
            .associateBy { it.id }
    }

    /**
     * Return the [Claim] identified by [id] or null.
     *
     * Note: This operation is optimized to be called inside loops as it is meant to be consumed by the entity to
     * business mapper.
     */
    fun findById(id: String): Claim? {
        return cachedClaimsMap[id]
    }

    /**
     * Return all [Claim] enabled on this authorization server.
     */
    fun listClaims(): List<Claim> {
        return cachedClaimsMap.values.toList()
    }

    /**
     * Return all [Claim] required to be provided by the end-user during its authorization flow.
     */
    fun listRequiredClaims(): List<Claim> {
        return listClaims().filter(Claim::required)
    }

    /**
     * Return all the [StandardClaim] enabled on this authorization server.
     */
    fun listStandardClaims(): List<StandardClaim> {
        return cachedClaimsMap.values.filterIsInstance<StandardClaim>()
    }
}
