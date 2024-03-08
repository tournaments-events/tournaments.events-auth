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

    /**
     * Throws a [LocalizedHttpException] if any of the [claims] conflict with another user login.
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
            throw httpExceptionOf(BAD_REQUEST, "sign_up.existing")
        }
    }

    suspend fun checkIfSignUpIsComplete(
        user: User,
        collectedClaims: List<CollectedClaim>
    ): SignInOrSignUpResult {
        // TODO: Check if we have verified the email.
        // TODO: Check if we have collected enough claim to continue.
        return SignInOrSignUpResult(
            user = user,
            complete = true
        )
    }
}

data class SignInResult(
    val user: User,
    /**
     * True if the sign-up is complete and the user can be redirected to the client.
     */
    val complete: Boolean
)

data class SignInOrSignUpResult(
    val user: User,
    /**
     * True if the sign-up is complete and the user can be redirected to the client.
     */
    val complete: Boolean
)
