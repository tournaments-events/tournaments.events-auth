package tournament.events.auth.config.properties

import io.micronaut.context.annotation.ConfigurationProperties
import tournament.events.auth.config.properties.PasswordAuthConfigurationProperties.Companion.PASSWORD_AUTH_KEY

@ConfigurationProperties(PASSWORD_AUTH_KEY)
interface PasswordAuthConfigurationProperties {
    val enabled: String?
    val loginClaims: List<String>?

    companion object {
        const val PASSWORD_AUTH_KEY = "password-auth"
    }
}
