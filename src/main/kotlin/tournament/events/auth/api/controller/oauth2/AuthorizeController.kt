package tournament.events.auth.api.controller.oauth2

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus.BAD_REQUEST
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.reactivex.rxjava3.core.Single
import tournament.events.auth.business.exception.singleBusinessExceptionOf
import tournament.events.auth.business.manager.AuthorizationCodeManager
import java.net.URI
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

// http://localhost:8080/api/oauth2/authorize?response_type=code&client_id=core&state=test

@Controller("/api/oauth2/authorize")
open class AuthorizeController(
    private val authorizationCodeManager: AuthorizationCodeManager
) {

    @Get
    open fun authorize(
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
    ): Single<HttpResponse<String>> {
        return when (responseType) {
            "code" -> authorizeWithCodeFlow(
                clientId = clientId!!,
                state = state!!
            )
            else -> singleBusinessExceptionOf(BAD_REQUEST, "exception.authorize.unsupported_response_type")
        }
    }

    internal fun authorizeWithCodeFlow(
        clientId: String,
        state: String
    ): Single<HttpResponse<String>> {
        val redirectUri = URI("/login?state=${state}")
        val response = HttpResponse.redirect<String>(redirectUri)

        return authorizationCodeManager.authorize(clientId, state)
            .andThen(Single.just(response))
    }
}
