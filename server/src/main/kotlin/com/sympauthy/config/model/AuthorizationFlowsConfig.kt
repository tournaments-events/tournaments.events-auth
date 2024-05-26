package com.sympauthy.config.model

import com.sympauthy.business.model.flow.AuthorizationFlow
import com.sympauthy.config.exception.ConfigurationException

sealed class AuthorizationFlowsConfig(
    configurationErrors: List<ConfigurationException>? = null
) : Config(configurationErrors)

class DisabledAuthorizationFlowsConfig(
    configurationErrors: List<ConfigurationException>
) : AuthorizationFlowsConfig(configurationErrors)

class EnabledAuthorizationFlowsConfig(
    val flows: List<AuthorizationFlow>
) : AuthorizationFlowsConfig()

fun AuthorizationFlowsConfig.orThrow(): EnabledAuthorizationFlowsConfig {
    return when (this) {
        is EnabledAuthorizationFlowsConfig -> this
        is DisabledAuthorizationFlowsConfig -> throw this.invalidConfig
    }
}
