package com.sympauthy.business.manager

import com.sympauthy.business.mapper.ClaimValueMapper
import com.sympauthy.business.model.user.CollectedClaim
import com.sympauthy.business.model.user.CollectedClaimUpdate
import com.sympauthy.business.model.user.User
import com.sympauthy.data.repository.CollectedClaimRepository
import com.sympauthy.data.repository.findAnyClaimMatching
import com.sympauthy.exception.httpExceptionOf
import io.micronaut.http.HttpStatus.BAD_REQUEST
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlin.jvm.optionals.getOrNull

@Singleton
class SignUpFlowManager(
    @Inject private val collectedClaimRepository: CollectedClaimRepository,
    @Inject private val claimValueMapper: ClaimValueMapper
) {

    internal suspend fun checkForConflictingUsers(claims: List<CollectedClaimUpdate>) {
        val claimIds = claims.map { it.claim.id }
        val values = claims
            .mapNotNull { it.value?.getOrNull() }
            .mapNotNull(claimValueMapper::toEntity)
        val existingCollectedClaims = collectedClaimRepository.findAnyClaimMatching(claimIds, values)
        if (existingCollectedClaims.isNotEmpty()) {
            throw httpExceptionOf(BAD_REQUEST, "sign_up.existing")
        }
    }

    suspend fun checkIfSignUpIsComplete(
        user: User,
        collectedClaims: List<CollectedClaim>
    ): SignUpResult {
        // TODO: Check if we have verified the email.
        // TODO: Check if we have collected enough claim to continue.
        return SignUpResult(
            user = user,
            complete = true
        )
    }
}

data class SignUpResult(
    val user: User,
    /**
     * True if the sign-up is complete and the user can be redirected to the client.
     */
    val complete: Boolean
)
