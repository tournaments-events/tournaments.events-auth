package com.sympauthy.config.properties

import com.sympauthy.config.properties.AdvancedConfigurationProperties.Companion.ADVANCED_KEY
import com.sympauthy.config.properties.ValidationCodeConfigurationProperties.Companion.VALIDATION_CODE_KEY
import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties(VALIDATION_CODE_KEY)
interface ValidationCodeConfigurationProperties {
    val expiration: String?
    val length: String?
    val resendDelay: String?

    companion object {
        const val VALIDATION_CODE_KEY = "$ADVANCED_KEY.validation-code"
    }
}
