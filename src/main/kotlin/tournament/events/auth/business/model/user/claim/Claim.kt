package tournament.events.auth.business.model.user.claim

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
     * True if the end-user MUST provide a value for this claim before completing any authentication flow
     * (either password or third-party providers).
     */
    val required: Boolean
) {

    /**
     * List of scope tokens that can be granted by the user to the client to read the claim.
     */
    abstract val readScopeTokens: Set<String>

    /**
     * List of scope tokens that can be granted by the user to the client to edit the claim.
     */
    abstract val writeScopeTokens: Set<String>

    /**
     * Return true if one of the [scopeTokens] allow to read the claim.
     */
    fun canBeRead(scopeTokens: List<String>): Boolean {
        return scopeTokens.any { readScopeTokens.contains(it) }
    }

    /**
     * Return true if one of the [scopeTokens] allow to write the claim.
     */
    fun canBeWritten(scopeTokens: List<String>): Boolean {
        return scopeTokens.any { writeScopeTokens.contains(it) }
    }
}
