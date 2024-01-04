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
        val errors = mutableListOf<ConfigurationException>()

        val root = try {
            // TODO: Add method to determine root url
            parser.getAbsoluteUriOrThrow(
                properties, "$URLS_KEY.root",
                UrlsConfigurationProperties::root
            )
        } catch (e: ConfigurationException) {
            errors.add(e)
            null
        }

        val signIn = try {
            // FIXME Allow to be relative to root
            // FIXME Default value
            parser.getAbsoluteUriOrThrow(
                properties, "$URLS_KEY.sign-in",
                UrlsConfigurationProperties::signIn
            )
        } catch (e: ConfigurationException) {
            errors.add(e)
            null
        }

        return if (errors.isEmpty()) {
            EnabledUrlsConfig(
                root = root!!,
                signIn = signIn!!
            )
        } else {
            DisabledUrlsConfig(
                configurationErrors = errors
            )
        }
    }
}
