package com.sympauthy.config.properties

import com.sympauthy.config.properties.AuthorizationFlowConfigurationProperties.Companion.AUTHORIZATION_FLOWS_KEY
import io.micronaut.context.annotation.EachProperty
import io.micronaut.context.annotation.Parameter


@EachProperty(AUTHORIZATION_FLOWS_KEY)
class AuthorizationFlowConfigurationProperties(
    @param:Parameter val id: String
) {
    var type: String? = null

    // Properties for web flows
    var root: String? = null
    var signIn: String? = null
    var collectClaims: String? = null
    var validateCode: String? = null
    var error: String? = null

    companion object {
        const val AUTHORIZATION_FLOWS_KEY = "flows"
    }
}
