package com.sympauthy.config.factory

import com.sympauthy.config.ConfigParser
import com.sympauthy.config.exception.ConfigurationException
import com.sympauthy.config.model.DisabledUIConfig
import com.sympauthy.config.model.EnabledUIConfig
import com.sympauthy.config.model.UIConfig
import com.sympauthy.config.properties.UIConfigurationProperties
import com.sympauthy.config.properties.UIConfigurationProperties.Companion.UI_KEY
import io.micronaut.context.annotation.Factory
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Factory
class UIConfigFactory(
    @Inject private val parser: ConfigParser
) {

    @Singleton
    fun provideUIConfig(
        properties: UIConfigurationProperties
    ): UIConfig {
        val errors = mutableListOf<ConfigurationException>()

        val displayName = try {
            parser.getStringOrThrow(
                properties, "${UI_KEY}.display-name",
                UIConfigurationProperties::displayName
            )
        } catch (e: ConfigurationException) {
            errors.add(e)
            null
        }

        return if (errors.isEmpty()) {
            EnabledUIConfig(
                displayName = displayName!!
            )
        } else {
            DisabledUIConfig(
                configurationErrors = errors
            )
        }
    }
}
