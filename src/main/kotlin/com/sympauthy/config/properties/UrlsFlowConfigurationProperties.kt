package com.sympauthy.config.properties

import com.sympauthy.config.properties.UrlsConfigurationProperties.Companion.URLS_KEY
import com.sympauthy.config.properties.UrlsFlowConfigurationProperties.Companion.FLOW_KEY
import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties(FLOW_KEY)
interface UrlsFlowConfigurationProperties {
    val signIn: String?
    val error: String?

    companion object {
        const val FLOW_KEY = "${URLS_KEY}.flow"
    }
}
