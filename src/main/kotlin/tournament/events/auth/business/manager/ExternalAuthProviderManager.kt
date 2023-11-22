package tournament.events.auth.business.manager

import io.micronaut.security.oauth2.client.OauthClient
import jakarta.inject.Singleton

@Singleton
class ExternalAuthProviderManager(
    private val oauthClients: List<OauthClient>
) {

    fun listProviders(): List<ExternalAuthProvider> {
        return oauthClients.map {
            ExternalAuthProvider(it.name)
        }
    }

    fun authorizeWithProvider(
        name: String,

    ) {

    }
}

data class ExternalAuthProvider(
    val name: String
)
