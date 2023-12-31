package tournament.events.auth.config.factory

import io.micronaut.context.annotation.Factory
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.config.ConfigParser
import tournament.events.auth.config.exception.ConfigurationException
import tournament.events.auth.config.model.AuthConfig
import tournament.events.auth.config.model.DisabledAuthConfig
import tournament.events.auth.config.model.EnabledAuthConfig
import tournament.events.auth.config.model.TokenConfig
import tournament.events.auth.config.properties.AuthConfigurationProperties
import tournament.events.auth.config.properties.AuthConfigurationProperties.Companion.AUTH_KEY
import tournament.events.auth.config.properties.TokenConfigurationProperties
import tournament.events.auth.config.properties.TokenConfigurationProperties.Companion.TOKEN_KEY
import java.time.Duration
import java.time.temporal.ChronoUnit

@Factory
class AuthConfigFactory(
    @Inject private val parser: ConfigParser
) {

    @Singleton
    fun provideAuthConfig(
        properties: AuthConfigurationProperties,
        tokenProperties: TokenConfigurationProperties?
    ): AuthConfig {
        val errors = mutableListOf<ConfigurationException>()

        val issuer = try {
            parser.getStringOrThrow(properties, "$AUTH_KEY.issue", AuthConfigurationProperties::issuer)
        } catch (e: ConfigurationException) {
            errors.add(e)
            null
        }

        val accessExpiration = try {
            tokenProperties?.let {
                parser.getDuration(it, "$TOKEN_KEY.access-expiration", TokenConfigurationProperties::accessExpiration)
            } ?: Duration.of(1, ChronoUnit.HOURS)
        } catch (e: ConfigurationException) {
            errors.add(e)
            null
        }

        val refreshEnabled = try {
            tokenProperties?.let {
                parser.getBoolean(it, "$TOKEN_KEY.refresh-enabled", TokenConfigurationProperties::refreshEnabled)
            } ?: false
        } catch (e: ConfigurationException) {
            errors.add(e)
            null
        }

        val refreshExpiration = try {
            tokenProperties?.let {
                parser.getDuration(it, "$TOKEN_KEY.refresh-expiration", TokenConfigurationProperties::refreshExpiration)
            }
        } catch (e: ConfigurationException) {
            errors.add(e)
            null
        }

        return if (errors.isEmpty()) {
            EnabledAuthConfig(
                issuer = issuer!!,
                audience = properties.audience,
                token = TokenConfig(
                    accessExpiration = accessExpiration!!,
                    refreshEnabled = refreshEnabled!!,
                    refreshExpiration = refreshExpiration
                )
            )
        } else {
            DisabledAuthConfig(errors)
        }
    }
}
