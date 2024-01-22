package tournament.events.auth.business.model.user.claim

/**
 * A claim, defined in the OpenID specification, that is collected by this authorization server as a first-party.
 */
class StandardClaim(
    openIdClaim: OpenIdClaim,
    required: Boolean
) : Claim(
    id = openIdClaim.id,
    dataType = openIdClaim.dataType,
    required = required
) {
    override val readScopeTokens = setOf(openIdClaim.scope.id)

    override val writeScopeTokens = emptySet<String>()
}
