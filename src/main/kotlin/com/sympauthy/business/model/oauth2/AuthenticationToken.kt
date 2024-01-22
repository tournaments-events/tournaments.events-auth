package com.sympauthy.business.model.oauth2

import java.time.LocalDateTime
import java.util.*

data class AuthenticationToken(
    val id: UUID,
    val type: AuthenticationTokenType,
    val userId: UUID,
    val clientId: String,
    val scopeTokens: List<String>,
    /**
     * Identifies all the tokens generated during a "session" of the end-user:
     * - the "session" starts when the end-user attempts authorization flow.
     * - until the end-user manually logs out.
     * - or until all tokens associated to the "session" expired.
     *
     * All refreshed tokens will carry the same identifier as the on they are refresh from.
     * This allows to revoke all tokens associated to the "session" when the user tries to log-out.
     */
    val authorizeAttemptId: UUID,

    val revoked: Boolean,
    val issueDate: LocalDateTime,
    val expirationDate: LocalDateTime?
)
