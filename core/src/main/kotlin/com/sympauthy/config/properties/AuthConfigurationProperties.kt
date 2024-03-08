package com.sympauthy.config.properties

import com.sympauthy.config.properties.AuthConfigurationProperties.Companion.AUTH_KEY
import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties(AUTH_KEY)
interface AuthConfigurationProperties {
    val issuer: String?
    val audience: String?

    /**
     * Url where the user will be redirected after completing the authentication with a third-party client.
     *
     * The url must include:
     * - the schema
     * - the domain
     */
    val redirectUrl: String?

    companion object {
        const val AUTH_KEY = "auth"
    }
}
