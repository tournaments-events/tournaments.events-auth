package tournament.events.auth.business.manager.auth

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus.BAD_REQUEST
import io.micronaut.http.HttpStatus.INTERNAL_SERVER_ERROR
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import tournament.events.auth.business.exception.businessExceptionOf
import tournament.events.auth.business.manager.auth.oauth2.Oauth2ClientManager
import tournament.events.auth.business.model.oauth2.State
import tournament.events.auth.config.model.ClientConfig

@Singleton
class ClientManager(
    @Inject private val clients: List<ClientConfig>,
    @Inject private val oauth2ClientManager: Oauth2ClientManager
) {

    fun listAvailableClients(): Flow<ClientConfig> {
        return clients.asFlow()
    }

    suspend fun findClientById(id: String): ClientConfig {
        return listAvailableClients()
            .filter { it.id == id }
            .firstOrNull() ?: throw businessExceptionOf(BAD_REQUEST, "exception.client.missing")
    }

    suspend fun authorizeWithClient(
        id: String,
        state: State
    ): HttpResponse<*> {
        val client = findClientById(id)
        return this.authorizeWithClient(client, state)
    }

    internal suspend fun authorizeWithClient(
        client: ClientConfig,
        state: State
    ): HttpResponse<*> {
        return when {
            client.oauth2 != null -> oauth2ClientManager.authorizeWithProvider(client, state)
            else -> throw businessExceptionOf(
                INTERNAL_SERVER_ERROR, "exception.client.unsupported"
            )
        }
    }

    suspend fun getTokensWithCode(
        id: String,
        code: String,
        state: State
    ) {
        val client = findClientById(id)
        return this.getTokensWithCode(client, code)
    }

    suspend fun getTokensWithCode(
        client: ClientConfig,
        code: String
    ) {
        return when {
            client.oauth2 != null -> oauth2ClientManager.getTokensWithCode(client, code)
            else -> throw businessExceptionOf(
                INTERNAL_SERVER_ERROR, "exception.client.unsupported"
            )
        }
    }
}

