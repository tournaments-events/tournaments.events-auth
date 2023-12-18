package tournament.events.auth.config.factory

import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import tournament.events.auth.business.model.client.Client
import tournament.events.auth.config.properties.ClientConfigurationProperties

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
