package tournament.events.auth.config.properties

import io.micronaut.context.annotation.ConfigurationProperties
import tournament.events.auth.config.properties.UrlsConfigurationProperties.Companion.URLS_KEY

@ConfigurationProperties(URLS_KEY)
interface UrlsConfigurationProperties {
    val root: String?

    companion object {
        const val URLS_KEY = "urls"
    }
}
