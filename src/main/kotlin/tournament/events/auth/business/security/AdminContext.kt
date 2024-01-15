package tournament.events.auth.business.security

import tournament.events.auth.business.model.user.claim.Claim

/**
 * A context that allow to do anything.
 *
 * This is used internally to perform operation
 */
data object AdminContext : Context() {

    override fun canRead(claim: Claim): Boolean = true

    override fun canWrite(claim: Claim): Boolean = true
}
