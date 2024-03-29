package com.sympauthy.business.model.client

import java.net.URI

data class Client(
    val id: String,
    val secret: String,

    /**
     * List of [URI] that are authorized to be used as an authorize_uri for the OAuth2 authorize endpoint.
     *
     * If the list is null, then all redirect_uri are allowed.
     *
     * > [OAuth 2.0 Security Best Current Practice](https://www.ietf.org/archive/id/draft-ietf-oauth-security-topics-25.html#name-redirect-uri-validation-att)
     */
    val allowedRedirectUris: List<URI>? = null,

    val allowedScopes: List<String>? = null,
    val defaultScopes: List<String>? = null
)
