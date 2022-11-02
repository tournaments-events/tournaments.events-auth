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

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/api/oauth2/authorize")
open class AuthorizeController(
    private val authorizationCodeManager: AuthorizationCodeManager
) {

    @Get
    open fun authorize(
        @QueryValue("response_type")
        @NotNull @NotBlank
        responseType: String?,
        @QueryValue("client_id")
        clientId: String?,
        @QueryValue("redirect_uri")
        @NotNull @NotBlank
        redirectUri: String?,
        @QueryValue("scope")
        scope: String?,
        @QueryValue("state")
        @NotNull @NotBlank
        state: String?
    ): Single<HttpResponse<String>> {
        return when (responseType) {
            "code" -> authorizeWithCodeFlow(clientId!!, redirectUri!!, state!!)
            else -> singleBusinessExceptionOf(BAD_REQUEST, "exception.authorize.unsupported_response_type")
        }
    }

    internal fun authorizeWithCodeFlow(
        clientId: String,
        redirectUri: String,
        state: String
    ): Single<HttpResponse<String>> {
        val response = HttpResponse.redirect<String>(URI("/login"))
        return authorizationCodeManager.authorize(clientId, redirectUri, state)
            .andThen(Single.just(response))
    }
}
