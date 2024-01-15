package tournament.events.auth.config.factory

import io.micronaut.context.annotation.Factory
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.business.model.user.claim.OpenIdClaim
import tournament.events.auth.config.ConfigParser
import tournament.events.auth.config.exception.ConfigurationException
import tournament.events.auth.config.model.DisabledPasswordAuthConfig
import tournament.events.auth.config.model.EnabledPasswordAuthConfig
import tournament.events.auth.config.model.PasswordAuthConfig
import tournament.events.auth.config.properties.PasswordAuthConfigurationProperties
import tournament.events.auth.config.properties.PasswordAuthConfigurationProperties.Companion.PASSWORD_AUTH_KEY

@Factory
class PasswordAuthConfigFactory(
    @Inject private val parser: ConfigParser
) {

    @Singleton
    fun providePasswordAuthConfig(
        properties: PasswordAuthConfigurationProperties
    ): PasswordAuthConfig {
        val errors = mutableListOf<ConfigurationException>()

        val enabled = try {
            parser.getBoolean(
                properties, "$PASSWORD_AUTH_KEY.enabled",
                PasswordAuthConfigurationProperties::enabled
            ) ?: true
        } catch (e: ConfigurationException) {
            errors.add(e)
            null
        }

        val loginClaims = try {
            properties.loginClaims?.map {
                parser.convertToEnum("$PASSWORD_AUTH_KEY.login-claims", it)
            } ?: listOf(OpenIdClaim.EMAIL)
        } catch (e: ConfigurationException) {
            errors.add(e)
            null
        }

        return if (errors.isEmpty()) {
            EnabledPasswordAuthConfig(
                enabled = enabled!!,
                loginClaims = loginClaims!!
            )
        } else {
            DisabledPasswordAuthConfig(
                configurationErrors = errors
            )
        }
    }
}
