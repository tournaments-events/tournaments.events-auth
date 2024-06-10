package com.sympauthy.config.properties

import com.sympauthy.config.properties.UIConfigurationProperties.Companion.UI_KEY
import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties(UI_KEY)
interface UIConfigurationProperties {
    val displayName: String?

    companion object {
        const val UI_KEY = "ui"
    }
}
