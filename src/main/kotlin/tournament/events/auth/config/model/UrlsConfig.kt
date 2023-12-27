package tournament.events.auth.config.model

import tournament.events.auth.config.exception.ConfigurationException
import java.net.URI

sealed class UrlsConfig(
    configurationErrors: List<ConfigurationException>? = null
) : Config(configurationErrors)

class EnabledUrlsConfig(
    val root: URI
): UrlsConfig()

class DisabledUrlsConfig(
    configurationErrors: List<ConfigurationException>
) : UrlsConfig(configurationErrors)

fun UrlsConfig.orThrow(): EnabledUrlsConfig {
    return when (this) {
        is EnabledUrlsConfig -> this
        is DisabledUrlsConfig -> throw this.invalidConfig
    }
}
