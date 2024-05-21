package com.sympauthy.config.model

import com.sympauthy.config.exception.ConfigurationException

sealed class FeaturesConfig(
    configurationErrors: List<ConfigurationException>? = null
) : Config(configurationErrors)

data class EnabledFeaturesConfig(
    /**
     * Enable the validation of end-user's email.
     */
    val emailValidation: Boolean
) : FeaturesConfig()

class DisabledFeaturesConfig(
    configurationErrors: List<ConfigurationException>
) : FeaturesConfig(configurationErrors)

fun FeaturesConfig.orThrow(): EnabledFeaturesConfig {
    return when (this) {
        is EnabledFeaturesConfig -> this
        is DisabledFeaturesConfig -> throw this.invalidConfig
    }
}
