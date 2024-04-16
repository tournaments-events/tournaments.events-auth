package com.sympauthy.business.model.user.claim

sealed class Claim(
    /**
     * Identifier of the claim.
     */
    val id: String,
    /**
     * Identifier of the claim indicating the value of this claims has been verified by the authorization server.
     * Ex. email_verified for the standard email claim.
     */
    val verifiedId: String?,
    /**
     * Type of the claim.
     */
    val dataType: ClaimDataType,
    /**
     *
     */
    val group: ClaimGroup?,
    /**
     * True if the end-user MUST provide a value for this claim before completing any authentication flow
     * (either password or third-party providers).
     */
    val required: Boolean
) {

    /**
     * List of scopes that can read the claim.
     */
    abstract val readScopes: Set<String>

    /**
     * List of scopes that can edit the claim.
     */
    abstract val writeScopes: Set<String>

    /**
     * Return true if one of the [scopes] allows to read the claim.
     */
    fun canBeRead(scopes: List<String>): Boolean {
        return scopes.any { readScopes.contains(it) }
    }

    /**
     * Return true if one of the [scopes] allows to edit the claim.
     */
    fun canBeWritten(scopes: List<String>): Boolean {
        return scopes.any { writeScopes.contains(it) }
    }
}
