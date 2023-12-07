package tournament.events.auth.business.manager.auth

import io.micronaut.http.HttpStatus.INTERNAL_SERVER_ERROR
import io.micronaut.http.uri.UriBuilder
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.business.exception.businessExceptionOf
import tournament.events.auth.business.exception.orMissingConfig
import tournament.events.auth.config.model.AuthConfig
import java.net.URI

@Singleton
class AuthManager(
    @Inject private val authConfig: AuthConfig
) {

    fun getRedirectUri(): URI {
        val authorizeUri = authConfig.redirectUrl?.let(UriBuilder::of)
            ?.build()
            .orMissingConfig("auth.redirect-uri")
        if (authorizeUri.scheme.isNullOrBlank() || authorizeUri.host.isNullOrBlank()) {
            throw businessExceptionOf(
                INTERNAL_SERVER_ERROR, "exception.auth.redirect-uri.invalid"
            )
        }
        return authorizeUri
    }
}
