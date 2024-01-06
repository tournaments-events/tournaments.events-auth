package tournament.events.auth.config.properties

import io.micronaut.context.annotation.ConfigurationProperties
import tournament.events.auth.config.properties.UrlsConfigurationProperties.Companion.URLS_KEY
import tournament.events.auth.config.properties.UrlsFlowConfigurationProperties.Companion.FLOW_KEY

@ConfigurationProperties(FLOW_KEY)
interface UrlsFlowConfigurationProperties {
    val signIn: String?
    val error: String?

    companion object {
        const val FLOW_KEY = "${URLS_KEY}.flow"
    }
}
