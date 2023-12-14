package tournament.events.auth.client

import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.reactive.awaitFirst
import tournament.events.auth.business.model.provider.ProviderCredentials
import tournament.events.auth.business.model.provider.config.ProviderUserInfoConfig

@Singleton
class UserInfoEndpointClient(
    @Inject private val httpClient: HttpClient
) {

    suspend fun fetchUserInfo(
        userInfoConfig: ProviderUserInfoConfig,
        credentials: ProviderCredentials
    ): Map<*, *> {
        val httpRequest = HttpRequest.GET<String>(userInfoConfig.uri)
            .accept(MediaType.APPLICATION_JSON)
        credentials.authenticate(httpRequest)

        return try {
            httpClient.retrieve(httpRequest, Map::class.java)
                .awaitFirst()
        } catch (e: HttpClientResponseException) {
            throw e
        }
    }
}
