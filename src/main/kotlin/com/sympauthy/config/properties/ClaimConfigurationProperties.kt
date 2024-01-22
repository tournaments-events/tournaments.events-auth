package com.sympauthy.config.properties

import com.sympauthy.config.properties.ClaimConfigurationProperties.Companion.CLAIMS_KEY
import io.micronaut.context.annotation.EachProperty
import io.micronaut.context.annotation.Parameter

@EachProperty(CLAIMS_KEY)
class ClaimConfigurationProperties(
    @param:Parameter val id: String
) {
    var enabled: String? = null
    var required: String? = null

    companion object {
        const val CLAIMS_KEY = "claims"
    }
}
