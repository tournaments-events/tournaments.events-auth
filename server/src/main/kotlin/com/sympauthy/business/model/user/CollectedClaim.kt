package com.sympauthy.business.model.user

import com.sympauthy.business.model.user.claim.Claim
import java.time.LocalDateTime
import java.util.*

/**
 * An information that this authorization server collected from the user as a first party.
 *
 * We consider being first party when:
 * - the claim is collected during an authentication flow.
 * - the claim is collected by a client and stored in this authorization server.
 */
data class CollectedClaim(
    val userId: UUID,
    val claim: Claim,
    /**
     * The value of the claim.
     * It may be null if the end-user has deliberately deleted the user info.
     */
    val value: Any?,
    /**
     * Whether the value of this claim has been verified by this authorization server or by the client.
     * null if the verification is not relevant for the claim.
     */
    val verified: Boolean?,
    val collectionDate: LocalDateTime,
    /**
     * When the value has been verified by this authorization server.
     * null if the verification is not relevant for the claim or has not been verified yet.
     */
    val verificationDate: LocalDateTime?
)
