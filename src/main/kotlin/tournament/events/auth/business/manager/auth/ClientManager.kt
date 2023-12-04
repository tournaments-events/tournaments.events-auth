package tournament.events.auth.business.manager.auth

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus.BAD_REQUEST
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import tournament.events.auth.business.exception.businessExceptionOf
import tournament.events.auth.config.model.ClientConfig

@Singleton
class ClientManager(
    @Inject private val clients: List<ClientConfig>,
    @Inject private val oauth2ClientManager: Oauth2ClientManager
) {

    fun listAvailableClients(): Flow<ClientConfig> {
        return clients.asFlow()
    }

    suspend fun findClient(name: String): ClientConfig {
        return listAvailableClients()
            .filter { it.name == name }
            .firstOrNull() ?: throw businessExceptionOf(BAD_REQUEST, "exception.client.missing")
    }

    suspend fun authorizeWithClient(
        name: String
    ): HttpResponse<*> {
        val client = findClient(name)
        return this.authorizeWithClient(client)
    }

    internal fun authorizeWithClient(
        clientConfig: ClientConfig
    ): HttpResponse<*> {
        return when {
            clientConfig.oauth != null -> TODO()
            else -> throw businessExceptionOf(
                BAD_REQUEST,
                "exception.client.missing"
            )
        }
    }
}

