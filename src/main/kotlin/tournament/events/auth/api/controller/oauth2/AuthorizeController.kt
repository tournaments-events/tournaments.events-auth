package tournament.events.auth.api.controller.oauth2

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus.BAD_REQUEST
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import tournament.events.auth.business.exception.businessExceptionOf
import tournament.events.auth.business.manager.auth.AuthorizeStateManager
import java.net.URI

// http://localhost:8092/api/oauth2/authorize?response_type=code&client_id=core&state=test

@Controller("/api/oauth2/authorize")
open class AuthorizeController(
    private val authorizeStateManager: AuthorizeStateManager
) {

    @Get
    suspend fun authorize(
        httpRequest: HttpRequest<*>,
        @QueryValue("response_type")
        responseType: String?,
        @QueryValue("client_id")
        clientId: String?,
        @QueryValue("redirect_uri")
        redirectUri: String?,
        @QueryValue("scope")
        scope: String?,
        @QueryValue("state")
        state: String?
    ): HttpResponse<String> {
        return when (responseType) {
            "code" -> authorizeWithCodeFlow(
                httpRequest = httpRequest,
                clientId = clientId!!,
                clientState = state!!,
                redirectUri = redirectUri!!
            )
            else -> throw businessExceptionOf(BAD_REQUEST, "exception.authorize.unsupported_response_type")
        }
    }

    internal suspend fun authorizeWithCodeFlow(
        httpRequest: HttpRequest<*>,
        clientId: String,
        clientState: String,
        redirectUri: String
    ): HttpResponse<String> {
        val state = authorizeStateManager.createState(
            httpRequest = httpRequest,
            clientId = clientId,
            clientState = clientState,
            redirectUri = redirectUri,
        )
        val encodedState = authorizeStateManager.encodeState(state)
        return HttpResponse.redirect(
            URI("/login?state=${encodedState}")
        )
    }
}
