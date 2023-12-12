package tournament.events.auth.business.model.oauth2

import io.micronaut.http.MutableHttpRequest
import tournament.events.auth.business.model.provider.ProviderCredentials

data class ProviderOauth2Tokens(
    val accessToken: String,
    val refreshToken: String?
) : ProviderCredentials {

    override fun <T> authenticate(httpRequest: MutableHttpRequest<T>) {
        httpRequest.bearerAuth(accessToken)
    }
}
