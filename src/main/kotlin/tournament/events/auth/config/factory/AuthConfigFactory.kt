package tournament.events.auth.config.factory

import io.micronaut.context.annotation.Factory
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.config.ConfigParser
import tournament.events.auth.config.exception.ConfigurationException
import tournament.events.auth.config.exception.configExceptionOf
import tournament.events.auth.config.model.AuthConfig
import tournament.events.auth.config.model.DisabledAuthConfig
import tournament.events.auth.config.model.EnabledAuthConfig
import tournament.events.auth.config.model.TokenConfig
import tournament.events.auth.config.properties.AuthConfigurationProperties
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
        return try {
            EnabledAuthConfig(
                issuer = properties.issuer,
                audience = properties.audience,
                token = getTokenConfig(
                    tokenProperties ?: throw configExceptionOf(TOKEN_KEY, "config.missing")
                )
            )
        } catch (exception: ConfigurationException) {
            DisabledAuthConfig(
                configurationErrors = listOf(exception)
            )
        }
    }

    fun getTokenConfig(
        properties: TokenConfigurationProperties
    ): TokenConfig {
        return TokenConfig(
            accessExpiration = parser.getDuration(properties, "$TOKEN_KEY.access-expiration") { it.accessExpiration } ?: Duration.of(1, ChronoUnit.HOURS),
            refreshEnabled = parser.getBoolean(properties, "$TOKEN_KEY.refresh-enabled") { it.refreshEnabled } ?: false,
            refreshExpiration = parser.getDuration(properties, "$TOKEN_KEY.refresh-expiration") { it.refreshExpiration }
        )
    }
}
