package tournament.events.auth.server.security

import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.authentication.BasicAuthUtils
import io.micronaut.security.filters.AuthenticationFetcher
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.reactive.publish
import org.reactivestreams.Publisher
import reactor.core.publisher.Mono
import tournament.events.auth.business.manager.ClientManager

/**
 * Authentication fetcher designed to authenticate the client for the Oauth2 token endpoint.
 *
 * It supports:
 * - Client id & client secret using Basic Auth.
 */
@Singleton
class ClientAuthenticationFetcher(
    @Inject private val clientManager: ClientManager
) : AuthenticationFetcher<HttpRequest<*>> {

    override fun fetchAuthentication(request: HttpRequest<*>): Publisher<Authentication> {
        if (request.uri.path != "/api/oauth2/token") {
            return Mono.empty()
        }
        return publish {
            getClientCredentialsFromRequest(request)
                ?.let { authenticateClient(it) }
                ?.let { send(it) }
        }
    }

    private fun getClientCredentialsFromRequest(request: HttpRequest<*>): ClientCredentials? {
        return sequenceOf(
            this::getClientCredentialsFromHeader
        ).firstNotNullOfOrNull { it.invoke(request) }
    }

    private fun getClientCredentialsFromHeader(request: HttpRequest<*>): ClientCredentials? {
        val credentials = request.headers.authorization
            ?.flatMap(BasicAuthUtils::parseCredentials)
            ?.orElse(null)
            ?: return null
        return ClientCredentials(
            clientId = credentials.username,
            clientSecret = credentials.password
        )
    }

    private fun authenticateClient(credentials: ClientCredentials?): Authentication? {
        if (credentials == null) {
            return null
        }
        val client = clientManager.authenticateClient(
            clientId = credentials.clientId,
            clientSecret = credentials.clientSecret
        )
        return client?.let(::ClientAuthentication)
    }
}

private data class ClientCredentials(
    val clientId: String,
    val clientSecret: String
)
