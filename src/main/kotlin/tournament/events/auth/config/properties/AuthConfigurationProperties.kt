package tournament.events.auth.config.properties

import io.micronaut.context.annotation.ConfigurationProperties
import tournament.events.auth.config.properties.AuthConfigurationProperties.Companion.AUTH_KEY

@ConfigurationProperties(AUTH_KEY)
interface AuthConfigurationProperties {
    val issuer: String?
    val audience: String?

    /**
     * Url where the user will be redirected after completing the authentication with a third-party client.
     *
     * The url must include:
     * - the schema
     * - the domain
     */
    val redirectUrl: String?

    companion object {
        const val AUTH_KEY = "auth"
    }
}
