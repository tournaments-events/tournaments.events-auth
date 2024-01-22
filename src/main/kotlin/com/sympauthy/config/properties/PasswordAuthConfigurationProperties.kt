package com.sympauthy.config.properties

import com.sympauthy.config.properties.PasswordAuthConfigurationProperties.Companion.PASSWORD_AUTH_KEY
import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties(PASSWORD_AUTH_KEY)
interface PasswordAuthConfigurationProperties {
    val enabled: String?
    val loginClaims: List<String>?

    companion object {
        const val PASSWORD_AUTH_KEY = "password-auth"
    }
}
