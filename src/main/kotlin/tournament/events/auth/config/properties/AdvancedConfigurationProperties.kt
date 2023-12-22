package tournament.events.auth.config.properties

import io.micronaut.context.annotation.ConfigurationProperties
import tournament.events.auth.config.properties.AdvancedConfigurationProperties.Companion.ADVANCED_KEY

@ConfigurationProperties(ADVANCED_KEY)
interface AdvancedConfigurationProperties {
    val userMergingStrategy: String?
    val keysGenerationStrategy: String?
    val jwtAlgorithm: String?

    companion object {
        const val ADVANCED_KEY = "advanced"
    }
}
