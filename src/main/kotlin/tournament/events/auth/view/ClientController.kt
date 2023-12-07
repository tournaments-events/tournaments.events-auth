package tournament.events.auth.view

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS
import jakarta.inject.Inject
import tournament.events.auth.business.manager.auth.AuthorizeStateManager
import tournament.events.auth.business.manager.auth.ClientManager

@Controller("/clients/{name}")
class ClientController(
    @Inject private val authorizeStateManager: AuthorizeStateManager,
    @Inject private val clientManager: ClientManager
) {

    @Get("authorize")
    @Secured(IS_ANONYMOUS)
    suspend fun authorize(
        name: String,
        @QueryValue("state") serializedState: String?
    ): HttpResponse<*> {
        val state = authorizeStateManager.verifyEncodedState(serializedState)
        return clientManager.authorizeWithClient(name, state)
    }

    @Get("callback")
    @Secured(IS_ANONYMOUS)
    suspend fun callback(
        name: String,
        code: String,
        @QueryValue("state") serializedState: String?
    ) {
        val state = authorizeStateManager.verifyEncodedState(serializedState)
    }
}
