package tournament.events.auth.view

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS
import io.micronaut.views.View
import tournament.events.auth.business.manager.UserManager

@Controller("/account")
@Secured(IS_ANONYMOUS)
class AccountController(
    private val userManager: UserManager
) {

    @Get("/create")
    @View("account-create")
    fun getCreateAccount(): Map<String, Any> {
        return emptyMap()
    }

    @Post("/create")
    @View("account-create")
    fun postCreateAccount(
        email: String?,
        password: String?,
        confirmPassword: String?
    ): Map<String, Any> {
        val model = mutableMapOf<String, Any>()
        return model
    }
}
