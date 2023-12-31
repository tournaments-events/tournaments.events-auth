package tournament.events.auth.api.controller.oauth2

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS
import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY
import io.swagger.v3.oas.annotations.media.Schema
import tournament.events.auth.api.exception.oauth2ExceptionOf
import tournament.events.auth.business.manager.auth.oauth2.AuthorizeManager
import tournament.events.auth.business.model.oauth2.OAuth2ErrorCode.UNSUPPORTED_RESPONSE_TYPE
import java.net.URI

@Controller("/api/oauth2/authorize")
@Secured(IS_ANONYMOUS)
open class AuthorizeController(
    private val authorizeManager: AuthorizeManager
) {

    @Operation(
        description = """
The authorization endpoint is used to interact with the resource owner and obtain an authorization grant.
""",
        tags = ["oauth2"],
        parameters = [
            Parameter(
                name = "response_type",
                `in` = QUERY,
                description = "",
                schema = Schema(
                    type = "string",
                    allowableValues = ["code"]
                )
            ),
            Parameter(
                name = "client_id",
                `in` = QUERY,
                description = "The identifier of the client that initiated the authentication grant.",
                schema = Schema(
                    type = "string"
                )
            ),
            Parameter(
                name = "scope",
                `in` = QUERY,
                description = "The scope of the access request.",
                schema = Schema(
                    type = "string"
                )
            ),
            Parameter(
                name = "state",
                `in` = QUERY,
                description = """
An opaque value used by the client to maintain state between the request and callback. 
The authorization server includes this value when redirecting the user-agent back to the client.
                """,
                schema = Schema(
                    type = "string"
                )
            ),
            Parameter(
                name = "redirect_uri",
                `in` = QUERY,
                description = "The url where the end-user must be redirected at the end of the authorization code grant flow.",
                schema = Schema(
                    type = "string"
                )
            )
        ],
        externalDocs = ExternalDocumentation(
            description = "Authorize Endpoint specification",
            url = "https://datatracker.ietf.org/doc/html/rfc6749#section-3.1"
        )
    )
    @Get
    suspend fun authorize(
        @QueryValue("response_type")
        responseType: String?,
        @QueryValue("client_id")
        clientId: String?,
        @QueryValue("redirect_uri")
        redirectUri: String?,
        @QueryValue("scope")
        scope: String?,
        @QueryValue("state")
        state: String?
    ): HttpResponse<String> {
        return when (responseType) {
            "code" -> authorizeWithCodeFlow(
                clientId = clientId!!,
                clientState = state!!,
                redirectUri = redirectUri!!
            )

            else -> throw oauth2ExceptionOf(
                UNSUPPORTED_RESPONSE_TYPE, "authorize.unsupported_response_type",
                "responseType" to responseType
            )
        }
    }

    internal suspend fun authorizeWithCodeFlow(
        clientId: String,
        clientState: String,
        redirectUri: String
    ): HttpResponse<String> {
        val state = authorizeManager.newAuthorizeAttempt(
            clientId = clientId,
            clientState = clientState,
            uncheckedRedirectUri = redirectUri,
        )
        val encodedState = authorizeManager.encodeState(state)
        return HttpResponse.redirect(
            URI("/login?state=${encodedState}")
        )
    }
}
