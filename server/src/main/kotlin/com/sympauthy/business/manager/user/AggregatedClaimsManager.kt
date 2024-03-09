package com.sympauthy.business.manager.user

import com.sympauthy.business.manager.provider.ProviderClaimsManager
import com.sympauthy.business.model.user.RawProviderClaims
import com.sympauthy.business.security.Context
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.*

/**
 * Manager providing methods to manipulate all claims collected about the user,
 * whether they have been collected by this authorization server as a first-party or by a third-party providers.
 */
@Singleton
class AggregatedClaimsManager(
    @Inject private val collectedClaimManager: CollectedClaimManager,
    @Inject private val providerClaimsManager: ProviderClaimsManager
) {

    /**
     * Merge all the claims collected about the user identified by [userId].
     * Depending on the scope contained in the [context], some information may be filtered as the client
     * is not granted access to them.
     */
    suspend fun aggregateClaims(
        context: Context,
        userId: UUID
    ): RawProviderClaims = coroutineScope {
        val deferredCollectedUserInfoList = async {
            collectedClaimManager.findReadableUserInfoByUserId(context, userId) // FIXME: use user context
        }
        val deferredProviderUserInfoList = async {
            providerClaimsManager.findByUserId(userId)
        }

        val merger = ClaimsMerger(
            userId = userId,
            collectedClaimList = deferredCollectedUserInfoList.await(),
            providerUserInfoList = deferredProviderUserInfoList.await()
        )
        merger.merge()
    }
}
