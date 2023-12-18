package tournament.events.auth.config.model

import tournament.events.auth.business.exception.BusinessException

sealed class AuthConfig(
    configurationErrors: List<BusinessException>? = null
): Config(configurationErrors)

class EnabledAuthConfig(
    val issuer: String?,
    val audience: String?
): AuthConfig()

class DisabledAuthConfig(
    configurationErrors: List<BusinessException>
): AuthConfig(configurationErrors)

fun AuthConfig.orThrow(): EnabledAuthConfig {
    return when (this) {
        is EnabledAuthConfig -> this
        is DisabledAuthConfig -> throw this.invalidConfig
    }
}
