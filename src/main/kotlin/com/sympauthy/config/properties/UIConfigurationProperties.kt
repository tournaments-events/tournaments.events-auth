package com.sympauthy.config.properties

import com.sympauthy.config.properties.UIConfigurationProperties.Companion.UI_KEYS
import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties(UI_KEYS)
interface UIConfigurationProperties {
    val displayName: String?

    companion object {
        const val UI_KEYS = "ui"
    }
}
