package com.sympauthy.business.manager.flow

import com.sympauthy.business.manager.user.CollectedClaimManager
import com.sympauthy.business.model.user.CollectedClaim
import com.sympauthy.business.model.user.User
import jakarta.inject.Inject
import jakarta.inject.Singleton

/**
 * Manager in charge of checking if the authentication flow of a user is completed.
 */
@Singleton
class AuthenticationFlowManager(
    @Inject private val collectedClaimManager: CollectedClaimManager
) {

    suspend fun checkIfAuthenticationIsComplete(
        user: User,
        collectedClaims: List<CollectedClaim>
    ): AuthenticationFlowResult {
        // TODO: Check if we have verified the email.
        val missingRequiredClaims = !collectedClaimManager.areAllRequiredClaimCollected(collectedClaims)
        val complete = listOf(missingRequiredClaims).none { it }
        return AuthenticationFlowResult(
            user = user,
            missingRequiredClaims = missingRequiredClaims,
            complete = complete
        )
    }
}

/**
 * The result of the authentication flow.
 */
data class AuthenticationFlowResult(
    /**
     * The user that has been authentication during the authentication flow.
     */
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
