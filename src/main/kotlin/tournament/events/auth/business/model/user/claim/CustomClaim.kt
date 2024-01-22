package tournament.events.auth.business.model.user.claim

class CustomClaim(
    id: String,
    dataType: ClaimDataType,
    required: Boolean
): Claim(
    id = id,
    dataType = dataType,
    required = required
) {
    override val readScopeTokens = emptySet<String>()

    override val writeScopeTokens = emptySet<String>()
}
