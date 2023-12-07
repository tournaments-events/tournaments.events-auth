package tournament.events.auth.config.model

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("auth")
interface AuthConfig {
    /**
     * Url where the user will be redirected after completing the authentication with a third-party client.
     *
     * The url must include:
     * - the schema
     * - the domain
     */
    val redirectUrl: String?
}
