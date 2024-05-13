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
import com.sympauthy.util.mergeUri
import com.sympauthy.view.UserFlowController.Companion.USER_FLOW_ENDPOINT
import io.micronaut.context.annotation.Factory
import io.micronaut.http.uri.UriBuilder
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.net.URI

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

        val signIn = if (root != null) {
            try {
                getFlowUri(
                    flowProperties, root, "$FLOW_KEY.sign-in",
                    UrlsFlowConfigurationProperties::signIn
                )
            } catch (e: ConfigurationException) {
                errors.add(e)
                null
            }
        } else null

        val collectMissingClaims = if (root != null) {
            try {
                getFlowUri(
                    flowProperties, root, "$FLOW_KEY.collect-claims",
                    UrlsFlowConfigurationProperties::collectClaims
                )
            } catch (e: ConfigurationException) {
                errors.add(e)
                null
            }
        } else null

        val error = if (root != null) {
            try {
                getFlowUri(
                    flowProperties, root, "$FLOW_KEY.error",
                    UrlsFlowConfigurationProperties::error
                )
            } catch (e: ConfigurationException) {
                errors.add(e)
                null
            }
        } else null

        return if (errors.isEmpty()) {
            EnabledUrlsConfig(
                root = root!!,
                flow = FlowUrlConfig(
                    signIn = signIn!!,
                    collectClaims = collectMissingClaims!!,
                    error = error!!
                )
            )
        } else {
            DisabledUrlsConfig(
                configurationErrors = errors
            )
        }
    }

    @Suppress("FoldInitializerAndIfToElvis")
    private fun getFlowUri(
        flowProperties: UrlsFlowConfigurationProperties,
        root: URI,
        key: String,
        value: (UrlsFlowConfigurationProperties) -> String?
    ): URI {
        val uri = parser.getUri(flowProperties, key, value)
        if (uri == null) {
            return getDefaultFlowUri(root, key) ?: throw ConfigurationException(key, "config.missing")
        }
        return mergeUri(root, uri)
    }

    private fun getDefaultFlowUri(root: URI, key: String): URI? {
        val userFlowUri = UriBuilder.of(root).path(USER_FLOW_ENDPOINT)
        return when (key) {
            "$FLOW_KEY.sign-in" -> userFlowUri.path("sign-in")
            "$FLOW_KEY.collect-claims" -> userFlowUri.path("collect-claims")
            "$FLOW_KEY.error" -> userFlowUri.path("error")
            else -> null
        }?.build()
    }
}
