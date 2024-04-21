package com.sympauthy.api.controller.flow

import com.sympauthy.api.controller.flow.ProvidersController.Companion.FLOW_PROVIDER_ENDPOINTS
import com.sympauthy.api.errorhandler.ExceptionConverter
import com.sympauthy.api.mapper.ErrorResourceMapper
import com.sympauthy.api.util.AuthorizationFlowRedirectBuilder
import com.sympauthy.api.util.AuthorizationFlowRedirectUriBuilder
import com.sympauthy.business.manager.auth.oauth2.AuthorizeManager
import com.sympauthy.business.manager.auth.oauth2.Oauth2ProviderManager
import com.sympauthy.business.manager.provider.ProviderConfigManager
import com.sympauthy.business.manager.provider.ProviderManager
import com.sympauthy.security.SecurityRule.HAS_VALID_STATE
import com.sympauthy.security.authorizeAttempt
import com.sympauthy.util.loggerForClass
import com.sympauthy.util.orDefault
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Error
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.inject.Inject

@Secured(HAS_VALID_STATE)
@Controller(FLOW_PROVIDER_ENDPOINTS)
class ProvidersController(
    @Inject private val authorizeManager: AuthorizeManager,
    @Inject private val oauth2ProviderManager: Oauth2ProviderManager,
    @Inject private val providerManager: ProviderManager,
    @Inject private val providerConfigManager: ProviderConfigManager,
    @Inject private val exceptionConverter: ExceptionConverter,
    @Inject private val redirectBuilder: AuthorizationFlowRedirectBuilder,
    @Inject private val redirectUriBuilder: AuthorizationFlowRedirectUriBuilder,
    @Inject private val errorResourceMapper: ErrorResourceMapper
) {

    private val logger = loggerForClass()

    @Operation(
        description = """
Redirect the end-user to the authorization flow of the provider identified by providerId.

If we cannot proceed with the redirection, instead the end-user will be redirect to the error page defined in ```urls.flow.error``` configuration.
Following query parameters will be populated with information about the error:
- ```error_code```: The technical identifier of this error.
- ```details```: A message containing technical details about the error.
- ```description```: (optional) A message explaining the error to the end-user. It may contain information on how to recover from the issue.
        """,
        responses = [
            ApiResponse(
                responseCode = "303",
                description = ""
            )
        ],
        tags = ["flow"]
    )
    @Get(FLOW_PROVIDER_AUTHORIZE_ENDPOINT)
    suspend fun authorizeWithProvider(
        authentication: Authentication,
        providerId: String
    ): HttpResponse<*> {
        // Check first to throw UNAUTHORIZED if authentication is not done through state.
        val authorizeAttempt = authentication.authorizeAttempt
        val provider = providerConfigManager.findEnabledProviderById(providerId)
        return providerManager.authorizeWithProvider(authorizeAttempt, provider)
    }

    @Get(FLOW_PROVIDER_CALLBACK_ENDPOINT)
    @Secured(IS_ANONYMOUS)
    suspend fun callback(
        providerId: String,
        @QueryValue("code") code: String?,
        @QueryValue("error") error: String?,
        @QueryValue("error_description") errorDescription: String?,
        @QueryValue("state") state: String?
    ): HttpResponse<*> {
        val authorizeAttempt = authorizeManager.verifyEncodedState(state)
        return if (code != null) {
            val provider = providerConfigManager.findEnabledProviderById(providerId)
            val result = oauth2ProviderManager.signInOrSignUpUsingProvider(
                authorizeAttempt = authorizeAttempt,
                provider = provider,
                authorizeCode = code
            )
            val url = redirectUriBuilder.getRedirectUri(
                attempt = authorizeAttempt,
                result = result,
                includeStates = true
            )
            HttpResponse.temporaryRedirect<Any>(url)
        } else {
            redirectBuilder.redirectToError(
                errorCode = error,
                details = errorDescription
            )
        }
    }

    /**
     * Since the user will be redirected to this page by the flow UI, in case of an error,
     * we need to redirect it back to flow UI with the details about the error.
     */
    @Error
    suspend fun redirectToErrorPage(
        request: HttpRequest<*>,
        throwable: Throwable
    ): HttpResponse<*> {
        val locale = request.locale.orDefault()

        val exception = exceptionConverter.normalize(throwable)
        if (exception.detailsId == "internal_server_error") {
            logger.error("Unexpected error occurred: ${throwable.message}", throwable)
        }

        val resource = errorResourceMapper.toResource(exception, locale)

        return redirectBuilder.redirectToError(
            errorCode = resource.errorCode,
            details = resource.details,
            description = resource.description
        )
    }

    companion object {
        const val FLOW_PROVIDER_ENDPOINTS = "/api/v1/flow/providers/{providerId}"
        const val FLOW_PROVIDER_AUTHORIZE_ENDPOINT = "/authorize"
        const val FLOW_PROVIDER_CALLBACK_ENDPOINT = "/callback"
    }
}
