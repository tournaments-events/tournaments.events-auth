package tournament.events.auth.business.security

import tournament.events.auth.business.model.oauth2.AuthorizeAttempt
import tournament.events.auth.business.model.user.claim.Claim

class StateContext(
    val authorizeAttempt: AuthorizeAttempt
): Context() {

    override fun canRead(claim: Claim): Boolean {
        return claim.canBeRead(authorizeAttempt.scopeTokens)
    }

    override fun canWrite(claim: Claim): Boolean {
        return claim.canBeWritten(authorizeAttempt.scopeTokens)
    }
}
