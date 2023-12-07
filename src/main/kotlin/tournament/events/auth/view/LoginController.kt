package tournament.events.auth.view

import io.micronaut.http.HttpRequest
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS
import io.micronaut.views.View
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import tournament.events.auth.business.manager.auth.AuthorizeStateManager
import tournament.events.auth.business.manager.auth.ClientManager
import tournament.events.auth.config.model.ClientConfig

@Controller("/login")
class LoginController(
    @Inject private val authorizeStateManager: AuthorizeStateManager,
    @Inject private val clientManager: ClientManager
) {

    @Get
    @View("login")
    @Secured(IS_ANONYMOUS)
    suspend fun login(
        httpRequest: HttpRequest<*>,
        @QueryValue state: String?
    ): Map<String, *> = coroutineScope {
        val existingState = state?.let {
            authorizeStateManager.verifyEncodedState(it)
        }

        val asyncState = async {
            val currentState = existingState ?: authorizeStateManager.createState(
                httpRequest, "test", "test"
            )
            authorizeStateManager.encodeState(currentState)
        }

        val asyncClients = async {
            clientManager.listAvailableClients().toList()
                .sortedWith(compareBy(ClientConfig::name))
        }

        mapOf(
            "state" to asyncState.await(),
            "clients" to asyncClients.await()
        )
    }
}
