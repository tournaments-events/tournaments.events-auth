package tournament.events.auth.business.manager.auth

import io.micronaut.http.HttpStatus.BAD_REQUEST
import io.micronaut.http.uri.UriBuilder
import jakarta.inject.Singleton
import tournament.events.auth.business.exception.businessExceptionOf
import tournament.events.auth.business.model.oauth2.AuthorizeRedirectUri
import tournament.events.auth.business.model.oauth2.State
import tournament.events.auth.config.model.ClientConfig
import tournament.events.auth.config.model.ClientOauth2Config
import java.net.URI

@Singleton
class Oauth2ClientManager(

) {

    fun authorizeWithProvider(
        client: ClientConfig,
        state: State
    ): AuthorizeRedirectUri {
        val oauth2Config = getOauth2Client(client)
        return buildAuthorizeRedirectUri(
            oauth2Config = oauth2Config,
            authorizeUri = getAuthorizeUri(oauth2Config),
            redirectUri = getRedirectUri(),
            state = state
        )
    }

    internal fun getOauth2Client(client: ClientConfig): ClientOauth2Config {
        return client.oauth ?: throw businessExceptionOf(BAD_REQUEST, "exception.client.oauth2.unsupported")
    }

    fun getAuthorizeUri(oauth2Config: ClientOauth2Config): URI {
        val authorizeUri = oauth2Config.authorizationUrl?.let(UriBuilder::of)
            ?.build()
            ?: throw businessExceptionOf(
                BAD_REQUEST,
                "exception.client.oauth2.unsupported" // TODO
            )
        if (authorizeUri.scheme.isNullOrBlank() || authorizeUri.host.isNullOrBlank()) {
            throw businessExceptionOf(
                BAD_REQUEST,
                "exception.client.oauth2.unsupported" // TODO
            )
        }
        return authorizeUri
    }

    fun getRedirectUri(): URI {
        return TODO()
    }

    fun buildAuthorizeRedirectUri(
        oauth2Config: ClientOauth2Config,
        authorizeUri: URI,
        redirectUri: URI,
        state: State
    ): AuthorizeRedirectUri {
        val clientId = oauth2Config.clientId ?: throw businessExceptionOf(
            BAD_REQUEST,
            "exception.client.oauth2.unsupported" // TODO
        )
        val redirectUri = AuthorizeRedirectUri(
            authorizeUri = authorizeUri,
            clientId = clientId,
            scopes = oauth2Config.scopes,
            redirectUri = redirectUri,
            state = state.id.toString()
        )
        return redirectUri
    }
}
