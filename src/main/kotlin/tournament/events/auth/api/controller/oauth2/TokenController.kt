package tournament.events.auth.api.controller.oauth2

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS

@Controller("/api/oauth2/token")
@Secured(IS_ANONYMOUS)
class TokenController {

    @Get
    fun getTokens() {
        TODO()
    }
}
