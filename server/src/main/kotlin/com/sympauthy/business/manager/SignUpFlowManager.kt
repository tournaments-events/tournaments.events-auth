package com.sympauthy.business.manager

import com.sympauthy.business.exception.businessExceptionOf
import com.sympauthy.business.manager.user.CollectedClaimManager
import com.sympauthy.business.mapper.ClaimValueMapper
import com.sympauthy.business.model.user.CollectedClaim
import com.sympauthy.business.model.user.CollectedClaimUpdate
import com.sympauthy.business.model.user.User
import com.sympauthy.data.repository.CollectedClaimRepository
import com.sympauthy.data.repository.findAnyClaimMatching
import io.micronaut.http.HttpStatus.BAD_REQUEST
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlin.jvm.optionals.getOrNull

@Singleton
class SignUpFlowManager(
    @Inject private val collectedClaimManager: CollectedClaimManager,
    @Inject private val collectedClaimRepository: CollectedClaimRepository,
    @Inject private val claimValueMapper: ClaimValueMapper
) {

    /**
     * Throws a sign_up.existing error if any of the [claims] conflict with another user login.
     *
     * As a user can use any of the provided [claims] to login, we must ensure that the values are unique
     * to a user and across the claims.
     */
    suspend fun checkForConflictingUsers(claims: List<CollectedClaimUpdate>) {
        val claimIds = claims.map { it.claim.id }
        val values = claims
            .mapNotNull { it.value?.getOrNull() }
            .mapNotNull(claimValueMapper::toEntity)
        val existingCollectedClaims = collectedClaimRepository.findAnyClaimMatching(claimIds, values)
        if (existingCollectedClaims.isNotEmpty()) {
            throw businessExceptionOf("sign_up.existing", recommendedStatus = BAD_REQUEST)
        }
    }

    suspend fun checkIfSignUpIsComplete(
        user: User,
        collectedClaims: List<CollectedClaim>
    ): SignInOrSignUpResult {
        // TODO: Check if we have verified the email.
        val missingRequiredClaims = !collectedClaimManager.areAllRequiredClaimCollected(collectedClaims)
        val complete = listOf(missingRequiredClaims).none { it }
        return SignInOrSignUpResult(
            user = user,
            missingRequiredClaims = missingRequiredClaims,
            complete = complete
        )
    }
}

data class SignInOrSignUpResult(
    val user: User,
    /**
     * True if we are missing some required claims from the end-user and they must be collected by the client.
     */
    val missingRequiredClaims: Boolean,
    /**
     * True if the sign-up is complete and the user can be redirected to the client.
     */
    val complete: Boolean
)
