package tournament.events.auth.config.model

import tournament.events.auth.business.exception.BusinessException
import tournament.events.auth.business.manager.jwt.CryptoKeysGenerationStrategy
import tournament.events.auth.business.model.jwt.JwtAlgorithm
import tournament.events.auth.business.model.user.UserMergingStrategy

sealed class AdvancedConfig : Config()

class EnabledAdvancedConfig(
    val userMergingStrategy: UserMergingStrategy,
    val keysGenerationStrategy: CryptoKeysGenerationStrategy,
    val jwtAlgorithm: JwtAlgorithm
) : AdvancedConfig()

class DisabledAdvancedConfig(
    val errors: List<BusinessException>
) : AdvancedConfig()

fun AdvancedConfig.orThrow(): EnabledAdvancedConfig {
    return when (this) {
        is EnabledAdvancedConfig -> this
        is DisabledAdvancedConfig -> throw this.invalidConfig
    }
}
