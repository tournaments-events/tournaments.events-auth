package tournament.events.auth.config.properties

import io.micronaut.context.annotation.ConfigurationProperties
import tournament.events.auth.config.properties.UIConfigurationProperties.Companion.UI_KEYS

@ConfigurationProperties(UI_KEYS)
interface UIConfigurationProperties {
    val displayName: String?

    companion object {
        const val UI_KEYS = "ui"
    }
}
