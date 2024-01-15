package tournament.events.auth.config.model

import tournament.events.auth.business.model.user.claim.Claim
import tournament.events.auth.config.exception.ConfigurationException

sealed class ClaimsConfig(
    configurationErrors: List<ConfigurationException>? = null
) : Config(configurationErrors)

class EnabledClaimsConfig(
    val claims: List<Claim>
) : ClaimsConfig()

class DisabledClaimsConfig(
    configurationErrors: List<ConfigurationException>
) : ClaimsConfig(configurationErrors)

fun ClaimsConfig.orThrow(): EnabledClaimsConfig {
    return when (this) {
        is EnabledClaimsConfig -> this
        is DisabledClaimsConfig -> throw this.invalidConfig
    }
}
