package com.sympauthy.business.model.user.claim

class CustomClaim(
    id: String,
    dataType: ClaimDataType,
    required: Boolean
): Claim(
    id = id,
    dataType = dataType,
    group = null,
    required = required
) {
    override val readScopeTokens = emptySet<String>()

    override val writeScopeTokens = emptySet<String>()
}
