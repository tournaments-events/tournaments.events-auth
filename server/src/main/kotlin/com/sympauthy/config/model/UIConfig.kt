package com.sympauthy.config.model

import com.sympauthy.config.exception.ConfigurationException

sealed class UIConfig(
    configurationErrors: List<ConfigurationException>? = null
) : Config(configurationErrors)

data class EnabledUIConfig(
    val displayName: String,
) : UIConfig()

class DisabledUIConfig(
    configurationErrors: List<ConfigurationException>
) : UIConfig(configurationErrors)

fun UIConfig.orThrow(): EnabledUIConfig {
    return when (this) {
        is EnabledUIConfig -> this
        is DisabledUIConfig -> throw this.invalidConfig
    }
}
