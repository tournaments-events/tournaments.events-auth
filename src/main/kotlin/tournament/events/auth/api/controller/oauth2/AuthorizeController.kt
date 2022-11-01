package tournament.events.auth.api.controller.oauth2

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/api/oauth2/authorize")
open class AuthorizeController(
    // private val authorizeController: AuthorizeController
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
        @NotNull @NotBlank
        state: String
    ) {

    }
}
