package tournament.events.auth.config.properties

import io.micronaut.context.annotation.ConfigurationProperties
import tournament.events.auth.config.properties.JwtConfigurationProperties.Companion.JWT_KEY

@ConfigurationProperties(JWT_KEY)
interface JwtConfigurationProperties {
    val publicAlg: String?
    val privateAlg: String?

    companion object {
        const val JWT_KEY = "advanced.jwt"
    }
}
