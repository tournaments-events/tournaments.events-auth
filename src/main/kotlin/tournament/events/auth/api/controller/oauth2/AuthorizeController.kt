package tournament.events.auth.api.controller.oauth2

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS
import tournament.events.auth.api.exception.oauth2ExceptionOf
import tournament.events.auth.business.manager.auth.oauth2.AuthorizeManager
import tournament.events.auth.business.model.auth.oauth2.OAuth2ErrorCode.UNSUPPORTED_RESPONSE_TYPE
import java.net.URI

// http://localhost:8092/api/oauth2/authorize?response_type=code&client_id=core&state=test

@Controller("/api/oauth2/authorize")
@Secured(IS_ANONYMOUS)
open class AuthorizeController(
    private val authorizeManager: AuthorizeManager
) {

    @Get
    suspend fun authorize(
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
                clientId = clientId!!,
                clientState = state!!,
                redirectUri = redirectUri!!
            )

            else -> throw oauth2ExceptionOf(
                UNSUPPORTED_RESPONSE_TYPE, "authorize.unsupported_response_type",
                "responseType" to responseType
            )
        }
    }

    internal suspend fun authorizeWithCodeFlow(
        clientId: String,
        clientState: String,
        redirectUri: String
    ): HttpResponse<String> {
        val state = authorizeManager.newAuthorizeAttempt(
            clientId = clientId,
            clientState = clientState,
            uncheckedRedirectUri = redirectUri,
        )
        val encodedState = authorizeManager.encodeState(state)
        return HttpResponse.redirect(
            URI("/login?state=${encodedState}")
        )
    }
}
