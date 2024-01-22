package com.sympauthy.business.security

import com.sympauthy.business.model.oauth2.AuthorizeAttempt
import com.sympauthy.business.model.user.claim.Claim

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
