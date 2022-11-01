package tournament.events.auth.api.controller

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule

@Controller("/version")
@Secured(SecurityRule.IS_ANONYMOUS)
class VersionController {

    @Get
    fun get(): String = "1.0.0"
}
