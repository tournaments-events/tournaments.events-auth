package com.sympauthy.business.model.oauth2

import java.time.LocalDateTime
import java.util.*

/**
 * Hold every information about a user (or resource owner in OAuth2 terminologie) attempt to go through the
 * authorization code grant flow.
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1">Authorization code grant flow</a>
 */
data class AuthorizeAttempt(
    /**
     * An uniq identifier for the flow.
     */
    val id: UUID,
    /**
     * The identifier of the client that initiated the authentication.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-2.2">Client identifier</a>
     */
    val clientId: String,
    /**
     * Sanitized scopes requested by the client.
     * Un-allowed and invalid scopes have already been filtered out.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-3.3">Scope</a>
     */
    val requestedScopes: List<String>,
    /**
     * The identifier of the user that was connected at the end of the authentication flow.
     */
    val userId: UUID?,
    /*
     * The scopes that were granted to the user during the authentication flow.
     */
    val grantedScopes: List<String>?,
    /**
     * The URI where we must redirect the user once the authentication flow is finished.
     */
    val redirectUri: String,
    /**
     * The state passed by the client to the authorize endpoint.
     */
    val state: String? = null,
    /**
     * The nonce passed by the client to the authorize endpoint.
     */
    val nonce: String? = null,
    /**
     * When the authentication flow was initiated by the user.
     */
    val attemptDate: LocalDateTime,
    val expirationDate: LocalDateTime
) {
    val expired: Boolean = expirationDate.isBefore(LocalDateTime.now())
}
