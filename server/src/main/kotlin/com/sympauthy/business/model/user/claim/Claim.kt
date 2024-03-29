package com.sympauthy.business.model.user.claim

sealed class Claim(
    /**
     * Identifier of the claim.
     */
    val id: String,
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
     * Return true if one of the [scopeTokens] allows to edit the claim.
     */
    fun canBeWritten(scopeTokens: List<String>): Boolean {
        return scopeTokens.any { writeScopes.contains(it) }
    }
}
