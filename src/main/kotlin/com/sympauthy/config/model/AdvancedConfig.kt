package com.sympauthy.config.model

import com.sympauthy.business.manager.jwt.CryptoKeysGenerationStrategy
import com.sympauthy.business.model.jwt.JwtAlgorithm
import com.sympauthy.business.model.user.UserMergingStrategy
import com.sympauthy.config.exception.ConfigurationException

sealed class AdvancedConfig(
    configurationErrors: List<ConfigurationException>? = null
) : Config(configurationErrors)

class EnabledAdvancedConfig(
    val userMergingStrategy: UserMergingStrategy,
    val keysGenerationStrategy: CryptoKeysGenerationStrategy,
    val publicJwtAlgorithm: JwtAlgorithm,
    val privateJwtAlgorithm: JwtAlgorithm
) : AdvancedConfig()

class DisabledAdvancedConfig(
    configurationErrors: List<ConfigurationException>
) : AdvancedConfig(configurationErrors)

fun AdvancedConfig.orThrow(): EnabledAdvancedConfig {
    return when (this) {
        is EnabledAdvancedConfig -> this
        is DisabledAdvancedConfig -> throw this.invalidConfig
    }
}
