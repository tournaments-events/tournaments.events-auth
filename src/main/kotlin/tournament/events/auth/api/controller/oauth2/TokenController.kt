package tournament.events.auth.api.controller.oauth2

import io.micronaut.http.MediaType.APPLICATION_FORM_URLENCODED
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Part
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.swagger.v3.oas.annotations.Operation
import jakarta.inject.Inject
import tournament.events.auth.api.controller.oauth2.TokenController.Companion.OAUTH2_TOKEN_ENDPOINT
import tournament.events.auth.api.exception.oauth2ExceptionOf
import tournament.events.auth.api.resource.oauth2.TokenResource
import tournament.events.auth.business.manager.auth.oauth2.AuthorizeManager
import tournament.events.auth.business.manager.auth.oauth2.TokenManager
import tournament.events.auth.business.model.oauth2.AuthenticationTokenType.ACCESS
import tournament.events.auth.business.model.oauth2.AuthenticationTokenType.REFRESH
import tournament.events.auth.business.model.oauth2.OAuth2ErrorCode.INVALID_GRANT
import tournament.events.auth.business.model.oauth2.OAuth2ErrorCode.UNSUPPORTED_GRANT_TYPE
import tournament.events.auth.security.client
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset

@Controller(OAUTH2_TOKEN_ENDPOINT)
class TokenController(
    @Inject private val authorizeManager: AuthorizeManager,
    @Inject private val tokenManager: TokenManager
) {

    @Operation(
        tags = ["oauth2"]
    )
    @Secured("ROLE_CLIENT")
    @Post(consumes = [APPLICATION_FORM_URLENCODED])
    suspend fun getTokens(
        authentication: Authentication,
        @Part("grant_type") grantType: String?,
        @Part(CODE_PARAM) code: String?,
        @Part("redirect_uri") redirectUri: String?,
        @Part(REFRESH_TOKEN_PARAM) refreshToken: String?,
    ): TokenResource {
        return when (grantType) {
            "authorization_code" -> getTokensUsingAuthorizationCode(
                code = code,
                redirectUri = redirectUri
            )

            "refresh_token" -> getTokensUsingRefreshToken(
                authentication = authentication,
                encodedRefreshToken = refreshToken
            )

            else -> throw oauth2ExceptionOf(
                UNSUPPORTED_GRANT_TYPE, "token.unsupported_grant_type",
                "grantType" to grantType
            )
        }
    }

    private suspend fun getTokensUsingAuthorizationCode(
        code: String?,
        redirectUri: String?
    ): TokenResource {
        if (code.isNullOrBlank()) {
            throw oauth2ExceptionOf(INVALID_GRANT, "token.missing_param", "param" to CODE_PARAM)
        }
        val attempt = authorizeManager.findByCode(code) ?: throw oauth2ExceptionOf(
            INVALID_GRANT, "token.expired", "description.oauth2.expired"
        )
        if (attempt.redirectUri != redirectUri) {
            throw oauth2ExceptionOf(INVALID_GRANT, "token.non_matching_redirect_uri")
        }
        val (accessToken, refreshToken) = tokenManager.generateTokens(attempt)

        return TokenResource(
            accessToken = accessToken.token,
            tokenType = "bearer",
            expiredIn = accessToken.expirationDate?.let {
                Duration.between(
                    Instant.now(),
                    it.toInstant(ZoneOffset.UTC)
                ).toMillisPart()
            },
            refreshToken = refreshToken?.token
        )
    }

    private suspend fun getTokensUsingRefreshToken(
        authentication: Authentication,
        encodedRefreshToken: String?
    ): TokenResource {
        val client = authentication.client
        if (encodedRefreshToken.isNullOrBlank()) {
            throw oauth2ExceptionOf(INVALID_GRANT, "token.missing_param", "param" to REFRESH_TOKEN_PARAM)
        }
        val tokens = tokenManager.refreshToken(client, encodedRefreshToken)
        val accessToken = tokens.first { it.type == ACCESS }
        val refreshedRefreshToken = tokens.firstOrNull { it.type == REFRESH }
        return TokenResource(
            accessToken = accessToken.token,
            tokenType = "bearer",
            expiredIn = accessToken.expirationDate?.let {
                Duration.between(
                    Instant.now(),
                    it.toInstant(ZoneOffset.UTC)
                ).toMillisPart()
            },
            refreshToken = refreshedRefreshToken?.token ?: encodedRefreshToken
        )
    }

    companion object {
        const val OAUTH2_TOKEN_ENDPOINT = "/api/oauth2/token"
        const val CODE_PARAM = "code"
        const val REFRESH_TOKEN_PARAM = "refresh_token"
    }
}
