package tournament.events.auth.business.authmapper

import io.micronaut.security.authentication.AuthenticationResponse
import io.micronaut.security.oauth2.endpoint.authorization.state.State
import io.micronaut.security.oauth2.endpoint.token.response.OauthAuthenticationMapper
import io.micronaut.security.oauth2.endpoint.token.response.TokenResponse
import jakarta.inject.Named
import jakarta.inject.Singleton
import org.reactivestreams.Publisher

@Singleton
@Named("discord") // (1)
class DiscordOauthAuthenticationMapper: OauthAuthenticationMapper {
    override fun createAuthenticationResponse(
        tokenResponse: TokenResponse?,
        state: State?
    ): Publisher<AuthenticationResponse> {
        TODO("Not yet implemented")
    }
}
