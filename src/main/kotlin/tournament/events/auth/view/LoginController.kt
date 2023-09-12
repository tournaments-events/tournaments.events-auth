package tournament.events.auth.view

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS
import io.micronaut.views.View

@Controkller("/login")
class LoginController {

    @Get
    @View("login")
    fun login(
        @QueryValue("state")
        state: String?
    ): Map<String, Any> {
        return emptyMap()
    }
}
