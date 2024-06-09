package com.sympauthy.business.model.flow

import java.net.URI

sealed class AuthorizationFlow(
    val id: String
) {

    companion object {
        const val DEFAULT_AUTHORIZATION_FLOW_ID = "default"
    }
}

/**
 * Contains where the end-user must be redirected to go through the different step of the authorization flow.
 */
class WebAuthorizationFlow(
    id: String,
    /**
     * [URI] of the page allowing the user either to:
     * - authenticate by entering its credentials
     * - select a third-party provider that will authenticate him.
     */
    val signInUri: URI,
    /**
     * [URI] of the page in charge of collecting claims from the end-user claims.
     *
     * This page will be presented during the authentication flow whenever all required claims
     * are not collected for an end-user.
     */
    val collectClaimsUri: URI,
    /**
     * [URI] of the page in charge of collecting validation codes from the user to validate the claims that
     * requires to be verified (ex. email).
     *
     * This page will be skipped if none of the collected claims require validation.
     */
    val validateClaimsUri: URI,
    /**
     * [URI] of the page displaying an error to the end-user.
     */
    val errorUri: URI
) : AuthorizationFlow(
    id = id
)

enum class AuthorizationFlowType {
    /**
     * The user is redirected to web pages that are each in charge of a part of the authorization flow.
     */
    WEB
}
