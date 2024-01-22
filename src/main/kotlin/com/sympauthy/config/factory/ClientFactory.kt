package com.sympauthy.config.factory

import com.sympauthy.business.model.client.Client
import com.sympauthy.config.properties.ClientConfigurationProperties
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton

@Factory
class ClientFactory {

    @Singleton
    fun provideClients(configs: List<ClientConfigurationProperties>): List<Client> {
        return configs.map { config ->
            Client(
                id = config.id,
                secret = config.secret ?: TODO()
            )
        }
    }
}
