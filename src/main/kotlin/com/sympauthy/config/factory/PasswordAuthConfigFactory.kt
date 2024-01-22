package com.sympauthy.config.factory

import com.sympauthy.business.model.user.claim.OpenIdClaim
import com.sympauthy.config.ConfigParser
import com.sympauthy.config.exception.ConfigurationException
import com.sympauthy.config.model.DisabledPasswordAuthConfig
import com.sympauthy.config.model.EnabledPasswordAuthConfig
import com.sympauthy.config.model.PasswordAuthConfig
import com.sympauthy.config.properties.PasswordAuthConfigurationProperties
import com.sympauthy.config.properties.PasswordAuthConfigurationProperties.Companion.PASSWORD_AUTH_KEY
import io.micronaut.context.annotation.Factory
import jakarta.inject.Inject
import jakarta.inject.Singleton

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
