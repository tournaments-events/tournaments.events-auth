package tournament.events.auth.business.security

import tournament.events.auth.business.model.user.claim.Claim

/**
 * Represents a security context which determine:
 * - which operation can be performed.
 * - which data can be read/written.
 */
sealed class Context {

    /**
     * Return true if the [claim] can be read in this context.
     */
    abstract fun canRead(claim: Claim): Boolean

    /**
     * Return true if the [claim] can be written in this context.
     */
    abstract fun canWrite(claim: Claim): Boolean
}
