package com.sympauthy.business.manager

import com.sympauthy.business.model.user.CollectedClaimUpdate
import com.sympauthy.business.model.user.claim.Claim
import com.sympauthy.business.model.user.claim.StandardClaim
import com.sympauthy.config.model.ClaimsConfig
import com.sympauthy.config.model.orThrow
import com.sympauthy.exception.localizedExceptionOf
import com.sympauthy.util.nullIfBlank
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.*

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
     * Return all the [StandardClaim] enabled on this authorization server.
     */
    fun listStandardClaims(): List<StandardClaim> {
        return cachedClaimsMap.values.filterIsInstance<StandardClaim>()
    }

    /**
     * Return if the [value] is valid otherwise throws an exception.
     */
    fun validateAndConvertToUpdate(claim: Claim, value: Any?): CollectedClaimUpdate {
        if (value != null && claim.dataType.typeClass != value::class) {
            throw localizedExceptionOf(
                "claim.validate.invalid_type",
                "claim" to claim, "type" to claim.dataType
            )
        }
        return when(value) {
            null -> CollectedClaimUpdate(claim, Optional.empty())
            is String -> validateStringAndConvertToUpdate(claim, value)
            else -> throw localizedExceptionOf("claim.validate.unsupported_type", "claim" to claim)
        }
    }

    internal fun validateStringAndConvertToUpdate(claim: Claim, value: String): CollectedClaimUpdate {
        // FIXME add validation for email & phone number
        return CollectedClaimUpdate(claim, Optional.ofNullable(value.nullIfBlank()))
    }
}
