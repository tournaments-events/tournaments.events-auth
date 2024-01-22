package com.sympauthy.api.controller.oauth2

import com.sympauthy.api.controller.oauth2.AuthorizeController.Companion.OAUTH2_AUTHORIZE_ENDPOINT
import com.sympauthy.api.exception.OAuth2Exception
import com.sympauthy.api.exception.oauth2ExceptionOf
import com.sympauthy.business.manager.auth.oauth2.AuthorizeManager
import com.sympauthy.business.model.oauth2.OAuth2ErrorCode.INVALID_REQUEST
import com.sympauthy.business.model.oauth2.OAuth2ErrorCode.UNSUPPORTED_RESPONSE_TYPE
import com.sympauthy.config.model.UrlsConfig
import com.sympauthy.config.model.orThrow
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.uri.UriBuilder
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS
import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.inject.Inject

@Controller(OAUTH2_AUTHORIZE_ENDPOINT)
@Secured(IS_ANONYMOUS)
open class AuthorizeController(
    @Inject private val authorizeManager: AuthorizeManager,
    @Inject private val urlsConfig: UrlsConfig
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
        uncheckedClientId: String?,
        @QueryValue("redirect_uri")
        uncheckedRedirectUri: String?,
        @QueryValue("scope")
        scope: String?, // FIXME: Use scope
        @QueryValue("state")
        state: String?
    ): HttpResponse<String> {
        // FIXME Check client is registered
        val clientId = uncheckedClientId ?: throw OAuth2Exception(
            INVALID_REQUEST, "authorize.client_id.missing"
        )
        return when (responseType) {
            "code" -> authorizeWithCodeFlow(
                clientId = clientId,
                clientState = state,
                uncheckedRedirectUri = uncheckedRedirectUri
            )

            else -> throw oauth2ExceptionOf(
                UNSUPPORTED_RESPONSE_TYPE, "authorize.unsupported_response_type",
                "responseType" to responseType
            )
        }
    }

    internal suspend fun authorizeWithCodeFlow(
        clientId: String,
        clientState: String?,
        uncheckedRedirectUri: String?
    ): HttpResponse<String> {
        val builder = urlsConfig.orThrow().flow.signIn.let(UriBuilder::of)

        val state = authorizeManager.newAuthorizeAttempt(
            clientId = clientId,
            clientState = clientState,
            uncheckedRedirectUri = uncheckedRedirectUri,
        )
        val encodedState = authorizeManager.encodeState(state)

        return builder.queryParam("state", encodedState)
            .build()
            .let { HttpResponse.redirect(it) }
    }

    companion object {
        const val OAUTH2_AUTHORIZE_ENDPOINT = "/api/oauth2/authorize"
    }
}
