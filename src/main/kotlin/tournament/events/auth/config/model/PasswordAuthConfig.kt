package tournament.events.auth.config.model

import tournament.events.auth.business.model.user.StandardClaim
import tournament.events.auth.config.exception.ConfigurationException

sealed class PasswordAuthConfig(
    configurationErrors: List<ConfigurationException>? = null
) : Config(configurationErrors)

class EnabledPasswordAuthConfig(
    val enabled: Boolean,
    /**
     * List of [StandardClaim] that the user can use as a login to sign-in.
     */
    val loginClaims: List<StandardClaim>
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
