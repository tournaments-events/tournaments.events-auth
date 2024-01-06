package tournament.events.auth.api.controller.flow

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Error
import io.micronaut.http.annotation.Get
import io.micronaut.http.uri.UriBuilder
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.inject.Inject
import tournament.events.auth.api.controller.flow.ProvidersController.Companion.FLOW_AUTHORIZE_ENDPOINT
import tournament.events.auth.api.errorhandler.ExceptionConverter
import tournament.events.auth.api.mapper.ErrorResourceMapper
import tournament.events.auth.business.manager.provider.ProviderConfigManager
import tournament.events.auth.business.manager.provider.ProviderManager
import tournament.events.auth.config.model.UrlsConfig
import tournament.events.auth.config.model.orThrow
import tournament.events.auth.security.SecurityRule.HAS_VALID_STATE
import tournament.events.auth.security.authorizeAttempt
import tournament.events.auth.util.loggerForClass
import tournament.events.auth.util.orDefault

@Secured(HAS_VALID_STATE)
@Controller(FLOW_AUTHORIZE_ENDPOINT)
class ProvidersController(
    @Inject private val providerManager: ProviderManager,
    @Inject private val providerConfigManager: ProviderConfigManager,
    @Inject private val exceptionConverter: ExceptionConverter,
    @Inject private val urlsConfig: UrlsConfig,
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
    @Get
    suspend fun authorizeWithProvider(
        authentication: Authentication,
        providerId: String
    ): HttpResponse<*> {
        // Check first to throw UNAUTHORIZED if authentication is not done through state.
        val authorizeAttempt = authentication.authorizeAttempt
        val provider = providerConfigManager.findEnabledProviderById(providerId)
        return providerManager.authorizeWithProvider(provider, authorizeAttempt)
    }

    /**
     * Since the user will be redirected to this page by the flow UI, in case of an error,
     * we need to redirect it back to flow UI with the details about the error.
     */
    @Error
    fun redirectToErrorPage(
        request: HttpRequest<*>,
        throwable: Throwable
    ): HttpResponse<*> {
        val locale = request.locale.orDefault()

        val exception = exceptionConverter.normalize(throwable)
        if (exception.detailsId == "internal_server_error") {
            logger.error("Unexpected error occurred: ${throwable.message}", throwable)
        }

        val resource = errorResourceMapper.toResource(exception, locale)

        val redirectUrl = urlsConfig.orThrow().flow.error.let(UriBuilder::of)
            .apply {
                queryParam("error_code", resource.errorCode)
                resource.details?.let { queryParam("details", it) }
                resource.details?.let { queryParam("description", it) }
            }
            .build()

        return HttpResponse.temporaryRedirect<Any>(redirectUrl)
    }

    companion object {
        const val FLOW_AUTHORIZE_ENDPOINT = "/api/flow/1.0/providers/{providerId}"
    }
}
