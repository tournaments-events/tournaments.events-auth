package tournament.events.auth.business.model.user.claim

/**
 * A claim, defined in the OpenID specification, that is collected by this authorization server as a first-party.
 */
class StandardClaim(
    openIdClaim: OpenIdClaim
) : Claim() {
    override val id = openIdClaim.id

    override val dataType = openIdClaim.dataType

    override val readScopeTokens = setOf(openIdClaim.scope.id)

    override val writeScopeTokens = emptySet<String>()
}
