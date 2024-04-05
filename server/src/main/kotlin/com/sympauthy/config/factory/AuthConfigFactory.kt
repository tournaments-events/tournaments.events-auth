package com.sympauthy.config.factory

import com.sympauthy.config.ConfigParser
import com.sympauthy.config.exception.ConfigurationException
import com.sympauthy.config.model.AuthConfig
import com.sympauthy.config.model.DisabledAuthConfig
import com.sympauthy.config.model.EnabledAuthConfig
import com.sympauthy.config.model.TokenConfig
import com.sympauthy.config.properties.AuthConfigurationProperties
import com.sympauthy.config.properties.AuthConfigurationProperties.Companion.AUTH_KEY
import com.sympauthy.config.properties.TokenConfigurationProperties
import com.sympauthy.config.properties.TokenConfigurationProperties.Companion.TOKEN_KEY
import io.micronaut.context.annotation.Factory
import jakarta.inject.Inject
import jakarta.inject.Singleton

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
            parser.getStringOrThrow(properties, "$AUTH_KEY.issuer", AuthConfigurationProperties::issuer)
        } catch (e: ConfigurationException) {
            errors.add(e)
            null
        }

        val accessExpiration = try {
            tokenProperties?.let {
                parser.getDurationOrThrow(it, "$TOKEN_KEY.access-expiration", TokenConfigurationProperties::accessExpiration)
            }
        } catch (e: ConfigurationException) {
            errors.add(e)
            null
        }

        val refreshEnabled = try {
            tokenProperties?.let {
                parser.getBooleanOrThrow(it, "$TOKEN_KEY.refresh-enabled", TokenConfigurationProperties::refreshEnabled)
            }
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
