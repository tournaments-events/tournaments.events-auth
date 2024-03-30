package com.sympauthy.api.controller.oauth2

import com.sympauthy.api.controller.oauth2.AuthorizeController.Companion.OAUTH2_AUTHORIZE_ENDPOINT
import com.sympauthy.api.exception.oauth2ExceptionOf
import com.sympauthy.api.util.AuthorizationFlowRedirectBuilder
import com.sympauthy.business.manager.ClientManager
import com.sympauthy.business.manager.ScopeManager
import com.sympauthy.business.manager.auth.oauth2.AuthorizeManager
import com.sympauthy.business.model.client.Client
import com.sympauthy.business.model.oauth2.OAuth2ErrorCode.INVALID_REQUEST
import com.sympauthy.business.model.oauth2.OAuth2ErrorCode.UNSUPPORTED_RESPONSE_TYPE
import com.sympauthy.business.model.oauth2.Scope
import com.sympauthy.util.toAbsoluteUri
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
import jakarta.inject.Inject
import java.net.URI

@Controller(OAUTH2_AUTHORIZE_ENDPOINT)
@Secured(IS_ANONYMOUS)
open class AuthorizeController(
    @Inject private val authorizeManager: AuthorizeManager,
    @Inject private val clientManager: ClientManager,
    @Inject private val scopeManager: ScopeManager,
    @Inject private val responseBuilder: AuthorizationFlowRedirectBuilder
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
        uncheckedScope: String?,
        @QueryValue("state")
        state: String?
    ): HttpResponse<*> {
        val client = getClient(uncheckedClientId)
        val scopes = getScopes(uncheckedScope)
        val redirectUri = getRedirectUri(uncheckedRedirectUri)
        return when (responseType) {
            "code" -> authorizeWithCodeFlow(
                client = client,
                clientState = state,
                scopes = scopes,
                redirectUri = redirectUri
            )

            else -> throw oauth2ExceptionOf(
                UNSUPPORTED_RESPONSE_TYPE, "authorize.unsupported_response_type",
                "responseType" to responseType
            )
        }
    }

    private suspend fun getClient(clientId: String?): Client {
        if (clientId.isNullOrBlank()) {
            throw oauth2ExceptionOf(INVALID_REQUEST, "authorize.client_id.missing")
        }
        return clientManager.findClientById(clientId) ?: throw oauth2ExceptionOf(
            INVALID_REQUEST, "authorize.client_id.invalid", "description.oauth2.invalid",
            "clientId" to clientId
        )
    }

    private suspend fun getScopes(uncheckedScope: String?): List<Scope> {
        if (uncheckedScope.isNullOrBlank()) {
            return emptyList()
        }
        return uncheckedScope.split(" ")
            .filter { it.isNotBlank() }
            .map { scope ->
                scopeManager.find(scope) ?: throw oauth2ExceptionOf(
                    INVALID_REQUEST, "authorize.scope.invalid", "description.oauth2.invalid",
                    "scope" to scope
                )
            }
    }

    private fun getRedirectUri(redirectUri: String?): URI {
        if (redirectUri.isNullOrBlank()) {
            throw oauth2ExceptionOf(INVALID_REQUEST, "authorize.redirect_uri.missing")
        }
        return redirectUri.toAbsoluteUri() ?: throw oauth2ExceptionOf(
            INVALID_REQUEST, "authorize.redirect_uri.invalid", "description.oauth2.invalid"
        )
    }

    private suspend fun authorizeWithCodeFlow(
        client: Client,
        clientState: String?,
        scopes: List<Scope>?,
        redirectUri: URI
    ): HttpResponse<*> {
        val authorizeAttempt = authorizeManager.newAuthorizeAttempt(
            client = client,
            clientState = clientState,
            uncheckedScopes = scopes,
            uncheckedRedirectUri = redirectUri,
        )
        return responseBuilder.redirectToSignIn(authorizeAttempt)
    }

    companion object {
        const val OAUTH2_AUTHORIZE_ENDPOINT = "/api/oauth2/authorize"
    }
}
