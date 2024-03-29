package com.sympauthy.config.properties

import com.sympauthy.config.properties.ClientConfigurationProperties.Companion.CLIENTS_KEY
import io.micronaut.context.annotation.EachProperty
import io.micronaut.context.annotation.Parameter

/**
 * Configuration of a client application that will authenticate its users.
 */
@EachProperty(CLIENTS_KEY)
class ClientConfigurationProperties(
    @param:Parameter val id: String
) {
    var secret: String? = null
    var allowedRedirectUris: List<String>? = null
    var allowedScopes: List<String>? = null
    var defaultScopes: List<String>? = null

    companion object {
        const val CLIENTS_KEY = "clients"
    }
}
