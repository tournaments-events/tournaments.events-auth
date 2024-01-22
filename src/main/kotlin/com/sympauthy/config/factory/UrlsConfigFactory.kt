package com.sympauthy.config.factory

import com.sympauthy.config.ConfigParser
import com.sympauthy.config.exception.ConfigurationException
import com.sympauthy.config.model.DisabledUrlsConfig
import com.sympauthy.config.model.EnabledUrlsConfig
import com.sympauthy.config.model.FlowUrlConfig
import com.sympauthy.config.model.UrlsConfig
import com.sympauthy.config.properties.UrlsConfigurationProperties
import com.sympauthy.config.properties.UrlsConfigurationProperties.Companion.URLS_KEY
import com.sympauthy.config.properties.UrlsFlowConfigurationProperties
import com.sympauthy.config.properties.UrlsFlowConfigurationProperties.Companion.FLOW_KEY
import io.micronaut.context.annotation.Factory
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Factory
class UrlsConfigFactory(
    @Inject private val parser: ConfigParser
) {

    @Singleton
    fun provideUrlsConfig(
        properties: UrlsConfigurationProperties,
        flowProperties: UrlsFlowConfigurationProperties
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
                flowProperties, "$FLOW_KEY.sign-in",
                UrlsFlowConfigurationProperties::signIn
            )
        } catch (e: ConfigurationException) {
            errors.add(e)
            null
        }

        val error = try {
            // FIXME Allow to be relative to root
            // FIXME Default value
            parser.getAbsoluteUriOrThrow(
                flowProperties, "$FLOW_KEY.error",
                UrlsFlowConfigurationProperties::error
            )
        } catch (e: ConfigurationException) {
            errors.add(e)
            null
        }

        return if (errors.isEmpty()) {
            EnabledUrlsConfig(
                root = root!!,
                flow = FlowUrlConfig(
                    signIn = signIn!!,
                    error = error!!
                )
            )
        } else {
            DisabledUrlsConfig(
                configurationErrors = errors
            )
        }
    }
}
