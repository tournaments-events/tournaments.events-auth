package tournament.events.auth.config.model

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.EachProperty
import io.micronaut.context.annotation.Parameter

/**
 * Configuration of an external authentication provider (ex. Discord) that can be used to
 * create an account on tournaments.events.
 */
@EachProperty("clients")
class ClientConfig(
    /**
     * Identifier of the client.
     */
    @param:Parameter val id: String
) {
    /**
     * Display name of the client.
     */
    var name: String? = null

    /**
     * Configuration of display elements related to the client.
     * ex. url of the icon, colors used to display the client.
     */
    var ui: ClientUIConfig? = null

    /**
     * To be used the client must support the authorization code grant type
     */
    var oauth2: ClientOauth2Config? = null

    // Must be nested: https://github.com/micronaut-projects/micronaut-core/issues/2373
    @ConfigurationProperties("ui")
    interface ClientUIConfig {
        /**
         * CSS code of the color to use for the background when displaying a button redirecting
         * to the client login page.
         */
        val buttonBackground: String?

        /**
         * CSS code of the color to use as the text when displaying a button redirecting
         * to the client login page.
         */
        val buttonText: String?
    }

    // Must be nested: https://github.com/micronaut-projects/micronaut-core/issues/2373
    @ConfigurationProperties("oauth2")
    interface ClientOauth2Config {
        val clientId: String
        val clientSecret: String
        val scopes: List<String>?
        val authorizationUrl: String
    }
}
