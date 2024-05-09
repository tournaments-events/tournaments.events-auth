package com.sympauthy.config.properties

import com.sympauthy.config.properties.ClaimConfigurationProperties.Companion.CLAIMS_KEY
import io.micronaut.context.annotation.EachProperty
import io.micronaut.context.annotation.Parameter

@EachProperty(CLAIMS_KEY)
class ClaimConfigurationProperties(
    @param:Parameter val id: String
) {
    // Common to OpenID and custom claims
    var enabled: String? = null
    var required: String? = null
    var allowedValues: List<Any>? = null

    // For custom claims only
    var type: String? = null

    companion object {
        const val CLAIMS_KEY = "claims"
    }
}
