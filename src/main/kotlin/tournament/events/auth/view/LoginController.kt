package tournament.events.auth.view

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS
import io.micronaut.views.View
import tournament.events.auth.business.manager.ExternalAuthProvider
import tournament.events.auth.business.manager.ExternalAuthProviderManager

@Controller("/login")
class LoginController(
    private val externalAuthProviderManager: ExternalAuthProviderManager
) {

    @Get
    @View("login")
    @Secured(IS_ANONYMOUS)
    fun login(
        @QueryValue("state")
        state: String?
    ): Map<String, *> {
        val providers = externalAuthProviderManager.listProviders()
            .sortedBy(ExternalAuthProvider::name)

        return mapOf(
            "state" to state,
            "providers" to providers
        )
    }
}
