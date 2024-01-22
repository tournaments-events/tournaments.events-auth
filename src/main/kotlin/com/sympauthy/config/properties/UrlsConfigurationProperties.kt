package com.sympauthy.config.properties

import com.sympauthy.config.properties.UrlsConfigurationProperties.Companion.URLS_KEY
import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties(URLS_KEY)
interface UrlsConfigurationProperties {
    val root: String?

    companion object {
        const val URLS_KEY = "urls"
    }
}
