package tournament.events.auth.server.security

import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.filters.AuthenticationFetcher
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.reactive.publish
import org.reactivestreams.Publisher
import tournament.events.auth.business.manager.auth.oauth2.AuthorizeManager

/**
 * The authentication using a state is restricted to the flow API.
 */
@Singleton
class StateAuthenticationFetcher(
    @Inject private val authorizeManager: AuthorizeManager
) : AuthenticationFetcher<HttpRequest<*>> {

    override fun fetchAuthentication(request: HttpRequest<*>): Publisher<Authentication> {
        return publish {
            val state = request.parameters["state"]
            if (!request.path.startsWith("/api/flow") || state.isNullOrBlank()) {
                return@publish
            }

            val authorizeAttempt = try {
                authorizeManager.verifyEncodedState(state)
            } catch (t: Throwable) {
                this.close(cause = t)
                return@publish
            }
            val authentication = StateAuthentication(authorizeAttempt)
            this.send(authentication)
        }
    }
}
