package tournament.events.auth.api.controller.openid

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule.IS_AUTHENTICATED
import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.Operation
import jakarta.inject.Inject
import tournament.events.auth.api.controller.openid.OpenIdUserInfoController.Companion.OPENID_USERINFO_ENDPOINT
import tournament.events.auth.api.mapper.UserInfoResourceMapper
import tournament.events.auth.api.resource.openid.UserInfoResource
import tournament.events.auth.business.manager.user.UserInfoManager
import tournament.events.auth.business.security.AdminContext
import tournament.events.auth.security.userId

@Controller(OPENID_USERINFO_ENDPOINT)
@Secured(IS_AUTHENTICATED)
class OpenIdUserInfoController(
    @Inject private val userInfoManager: UserInfoManager,
    @Inject private val userInfoMapper: UserInfoResourceMapper
) {

    @Operation(
        description = "Retrieves the consented OpenID claims about the logged-in subject.",
        tags = ["openid"],
        externalDocs = ExternalDocumentation(
            url = "https://openid.net/specs/openid-connect-core-1_0.html#UserInfo"
        )
    )
    @Get
    suspend fun getUserInfo(
        authentication: Authentication
    ): UserInfoResource {
        val userInfo = userInfoManager.aggregateUserInfo(
            context = AdminContext, // FIXME Generate context from authentication
            userId = authentication.userId
        )
        return userInfoMapper.toResource(userInfo)
    }

    companion object {
        const val OPENID_USERINFO_ENDPOINT = "/api/openid/userinfo"
    }
}
