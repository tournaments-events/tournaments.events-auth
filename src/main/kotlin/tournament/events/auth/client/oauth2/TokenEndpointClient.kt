package tournament.events.auth.client.oauth2

import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.MediaType.APPLICATION_JSON
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.reactive.awaitFirst
import tournament.events.auth.business.model.provider.oauth2.ProviderOAuth2TokenRequest
import tournament.events.auth.client.oauth2.model.TokenEndpointResponse

@Singleton
class TokenEndpointClient(
    @Inject private val httpClient: HttpClient
) {


    suspend fun fetchTokens(request: ProviderOAuth2TokenRequest): TokenEndpointResponse {
        val tokenUri = request.oauth2.tokenUri
        val body = mutableMapOf(
            "grant_type" to "authorization_code",
            "code" to request.authorizeCode,
            "redirect_uri" to request.redirectUri
        )

        val httpRequest = HttpRequest
            .POST(tokenUri, Map::class.java)
            .accept(APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .basicAuth(request.oauth2.clientId, request.oauth2.clientSecret)
            .body(body)

        return try {
            httpClient.retrieve(httpRequest, TokenEndpointResponse::class.java)
                .awaitFirst()
        } catch (e: HttpClientResponseException) {
            throw e
        }
    }
}
