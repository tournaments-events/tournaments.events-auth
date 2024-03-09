package com.sympauthy.api.mapper

import com.sympauthy.business.manager.ClaimManager
import com.sympauthy.business.manager.user.CollectedClaimManager
import com.sympauthy.business.model.user.CollectedClaimUpdate
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class CollectedClaimUpdateMapper(
    @Inject private val claimManager: ClaimManager,
    @Inject private val collectedClaimManager: CollectedClaimManager
) {

    fun toUpdates(values: Map<String, Any?>): List<CollectedClaimUpdate> {
        return values.mapNotNull { (claimId, value) -> toUpdate(claimId, value) }
    }

    fun toUpdate(claimId: String, value: Any?): CollectedClaimUpdate? {
        val claim = claimManager.findById(claimId) ?: return null
        return collectedClaimManager.validateAndConvertToUpdate(claim, value)
    }
}
