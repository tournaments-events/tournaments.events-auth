package com.sympauthy.config.properties

import com.sympauthy.config.properties.AdvancedConfigurationProperties.Companion.ADVANCED_KEY
import com.sympauthy.config.properties.HashConfigurationProperties.Companion.HASH_KEY
import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties(HASH_KEY)
interface HashConfigurationProperties {
    val costParameter: String?
    val blockSize: String?
    val parallelizationParameter: String?
    val keyLength: String?
    val saltLength: String?

    companion object {
        const val HASH_KEY = "$ADVANCED_KEY.hash"
    }
}
