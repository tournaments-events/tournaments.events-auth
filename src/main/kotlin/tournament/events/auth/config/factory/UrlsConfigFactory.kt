package tournament.events.auth.config.factory

import io.micronaut.context.annotation.Factory
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.config.ConfigParser
import tournament.events.auth.config.exception.ConfigurationException
import tournament.events.auth.config.model.DisabledUrlsConfig
import tournament.events.auth.config.model.EnabledUrlsConfig
import tournament.events.auth.config.model.UrlsConfig
import tournament.events.auth.config.properties.UrlsConfigurationProperties
import tournament.events.auth.config.properties.UrlsConfigurationProperties.Companion.URLS_KEY

@Factory
class UrlsConfigFactory(
    @Inject private val parser: ConfigParser
) {

    @Singleton
    fun provideUrlsConfig(
        properties: UrlsConfigurationProperties
    ): UrlsConfig {
        return try {
            EnabledUrlsConfig(
                root = parser.getAbsoluteUriOrThrow(properties, "$URLS_KEY.root") { it.root }
            )
        } catch (exception: ConfigurationException) {
            DisabledUrlsConfig(
                configurationErrors = listOf(exception)
            )
        }
    }
}
