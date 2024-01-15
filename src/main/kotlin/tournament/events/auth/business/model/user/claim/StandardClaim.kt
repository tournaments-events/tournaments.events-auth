package tournament.events.auth.business.model.user.claim

class StandardClaim(
    openIdClaim: OpenIdClaim
) : Claim() {
    override val id = openIdClaim.id

    override val dataType = openIdClaim.dataType

    override val readScopeTokens = setOf(openIdClaim.scope.id)

    override val writeScopeTokens = emptySet<String>()
}
