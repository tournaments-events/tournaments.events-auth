package tournament.events.auth.business.manager.auth.oauth2

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus.BAD_REQUEST
import io.micronaut.http.uri.UriBuilder
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.business.exception.businessExceptionOf
import tournament.events.auth.business.manager.auth.AuthManager
import tournament.events.auth.business.manager.auth.AuthorizeStateManager
import tournament.events.auth.business.model.oauth2.AuthorizeRedirectUriBuilder
import tournament.events.auth.business.model.oauth2.State
import tournament.events.auth.config.model.ClientConfig
import tournament.events.auth.config.model.ClientConfig.ClientOauth2Config
import java.net.URI

@Singleton
class Oauth2ClientManager(
    @Inject private val authManager: AuthManager,
    @Inject private val stateManager: AuthorizeStateManager
) {

    suspend fun authorizeWithProvider(
        client: ClientConfig,
        state: State
    ): HttpResponse<*> {
        val oauth2Config = getOauth2Client(client)
        val redirectUri = buildAuthorizeRedirectUri(
            oauth2Config = oauth2Config,
            authorizeUri = getAuthorizeUri(oauth2Config),
            redirectUri = getRedirectUri(client),
            state = state
        ).build()
        return HttpResponse.redirect<Any>(redirectUri)
    }

    internal fun getOauth2Client(client: ClientConfig): ClientOauth2Config {
        return client.oauth2 ?: throw businessExceptionOf(BAD_REQUEST, "exception.client.oauth2.unsupported")
    }

    internal fun getRedirectUri(
        client: ClientConfig
    ): URI {
        return UriBuilder.of(authManager.getRedirectUri())
            .path("clients")
            .path(client.id)
            .path("callback")
            .build()
    }

    internal fun getAuthorizeUri(oauth2Config: ClientOauth2Config): URI {
        val authorizeUri = oauth2Config.authorizationUrl?.let(UriBuilder::of)
            ?.build()
            ?: throw businessExceptionOf(
                BAD_REQUEST, "exception.client.oauth2.unsupported" // TODO
            )
        if (authorizeUri.scheme.isNullOrBlank() || authorizeUri.host.isNullOrBlank()) {
            throw businessExceptionOf(
                BAD_REQUEST,
                "exception.client.oauth2.unsupported" // TODO
            )
        }
        return authorizeUri
    }

    internal suspend fun buildAuthorizeRedirectUri(
        oauth2Config: ClientOauth2Config,
        authorizeUri: URI,
        redirectUri: URI,
        state: State
    ): AuthorizeRedirectUriBuilder {
        return AuthorizeRedirectUriBuilder(
            authorizeUri = authorizeUri,
            responseType = "code",
            clientId = oauth2Config.clientId,
            clientSecret = oauth2Config.clientSecret,
            scopes = oauth2Config.scopes,
            redirectUri = redirectUri,
            state = stateManager.encodeState(state)
        )
    }

    suspend fun getTokensWithCode(
        client: ClientConfig,
        code: String
    ) {

    }
}
