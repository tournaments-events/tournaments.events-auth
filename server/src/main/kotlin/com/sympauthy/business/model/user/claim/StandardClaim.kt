package com.sympauthy.business.model.user.claim

/**
 * A claim, defined in the OpenID specification, that is collected by this authorization server as a first-party.
 */
class StandardClaim(
    openIdClaim: OpenIdClaim,
    required: Boolean
) : Claim(
    id = openIdClaim.id,
    verifiedId = openIdClaim.verifiedId,
    dataType = openIdClaim.type,
    group = openIdClaim.group,
    required = required,
    userInputted = !openIdClaim.generated
) {
    override val readScopes = setOf(openIdClaim.scope.scope)

    override val writeScopes = emptySet<String>()
}
