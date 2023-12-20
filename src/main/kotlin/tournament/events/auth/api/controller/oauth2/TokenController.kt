package tournament.events.auth.api.controller.oauth2

import io.micronaut.http.MediaType.APPLICATION_FORM_URLENCODED
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Part
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import tournament.events.auth.api.exception.oauth2ExceptionOf
import tournament.events.auth.api.model.oauth2.TokenResource
import tournament.events.auth.business.model.auth.oauth2.OAuth2ErrorCode

@Controller("/api/oauth2/token")
class TokenController {

    @Post(consumes = [APPLICATION_FORM_URLENCODED])
    @Secured("ROLE_CLIENT")
    suspend fun getTokens(
        authentication: Authentication,
        @Part("grant_type") grantType: String?,
        @Part("code") code: String?,
        @Part("redirect_uri") redirectUri: String?,
        @Part("refresh_token") refreshToken: String?,
    ): TokenResource {
        return when (grantType) {
            "authorization_code" -> getTokensUsingAuthorizationCode(
                code = code,
                redirectUri = redirectUri
            )

            "refresh_token" -> TODO()
            else -> throw oauth2ExceptionOf(
                OAuth2ErrorCode.SERVER_ERROR, "token.invalid_grant_type",
                "grantType" to grantType
            )
        }
    }

    private suspend fun getTokensUsingAuthorizationCode(
        code: String?,
        redirectUri: String?
    ): TokenResource {
        TODO()
    }

    private suspend fun getTokensUsingRefreshToken(
        clientId: String,
        clientSecret: String,
        refreshToken: String?
    ): TokenResource {
        TODO()
    }
}
