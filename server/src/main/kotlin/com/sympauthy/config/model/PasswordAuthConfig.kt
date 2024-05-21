package com.sympauthy.config.model

import com.sympauthy.business.model.user.claim.OpenIdClaim
import com.sympauthy.config.exception.ConfigurationException

sealed class PasswordAuthConfig(
    configurationErrors: List<ConfigurationException>? = null
) : Config(configurationErrors)

data class EnabledPasswordAuthConfig(
    val enabled: Boolean,
    /**
     * List of [OpenIdClaim] that the user can use as a login to sign-in.
     */
    val loginClaims: List<OpenIdClaim>
) : PasswordAuthConfig()

class DisabledPasswordAuthConfig(
    configurationErrors: List<ConfigurationException>
) : PasswordAuthConfig(configurationErrors)

fun PasswordAuthConfig.orThrow(): EnabledPasswordAuthConfig {
    return when (this) {
        is EnabledPasswordAuthConfig -> this
        is DisabledPasswordAuthConfig -> throw this.invalidConfig
    }
}
