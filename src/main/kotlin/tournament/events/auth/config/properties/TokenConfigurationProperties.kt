package tournament.events.auth.config.properties

import io.micronaut.context.annotation.ConfigurationProperties
import tournament.events.auth.config.properties.AuthConfigurationProperties.Companion.AUTH_KEY
import tournament.events.auth.config.properties.TokenConfigurationProperties.Companion.TOKEN_KEY

@ConfigurationProperties(TOKEN_KEY)
interface TokenConfigurationProperties {
    val accessExpiration: String?
    val refreshEnabled: String?
    val refreshExpiration: String?

    companion object {
        const val TOKEN_KEY = "$AUTH_KEY.token"
    }
}
