package tournament.events.auth.config.model

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.EachProperty
import io.micronaut.context.annotation.Parameter

/**
 * Configuration of an external authentication provider (ex. Discord).
 */
@EachProperty(ProviderConfig.PROVIDERS_CONFIG_KEY)
class ProviderConfig(
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
     *
     */
    var userInfo: UserInfoConfig? = null

    /**
     * To be used the client must support the authorization code grant type
     */
    var oauth2: Oauth2Config? = null

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

    // Only one level of nester properties are supported by Micronaut.
    @ConfigurationProperties("oauth2")
    interface Oauth2Config {
        val clientId: String?
        val clientSecret: String?
        val scopes: List<String>?

        val authorizationUrl: String?

        val tokenUrl: String?
        val tokenAuthMethod: String?
    }

    @ConfigurationProperties("user-info")
    interface UserInfoConfig {
        val url: String?
        val paths: Map<String, String?>?
    }

    companion object {
        const val PROVIDERS_CONFIG_KEY = "providers"
    }
}
