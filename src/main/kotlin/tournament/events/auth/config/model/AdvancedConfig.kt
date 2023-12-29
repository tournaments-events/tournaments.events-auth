package tournament.events.auth.config.model

import tournament.events.auth.business.manager.jwt.CryptoKeysGenerationStrategy
import tournament.events.auth.business.model.jwt.JwtAlgorithm
import tournament.events.auth.business.model.user.UserMergingStrategy
import tournament.events.auth.config.exception.ConfigurationException

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
