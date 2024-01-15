package tournament.events.auth.business.model.user.claim

class CustomClaim(
    override val id: String,
    override val dataType: ClaimDataType
): Claim() {

    override val readScopeTokens = emptySet<String>()

    override val writeScopeTokens = emptySet<String>()
}
