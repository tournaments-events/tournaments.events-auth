package com.sympauthy.api.controller.flow

import com.sympauthy.api.mapper.flow.ClaimsValidationFlowResultResourceMapper
import com.sympauthy.api.resource.flow.ClaimValidationInputResource
import com.sympauthy.api.resource.flow.ClaimsValidationFlowResultResource
import com.sympauthy.api.util.flow.FlowControllerHelper
import com.sympauthy.business.manager.flow.AuthorizationFlowClaimValidationManager
import com.sympauthy.business.manager.flow.AuthorizationFlowManager
import com.sympauthy.business.manager.flow.WebAuthorizationFlowRedirectUriBuilder
import com.sympauthy.business.manager.user.CollectedClaimManager
import com.sympauthy.business.model.code.ValidationCodeMedia
import com.sympauthy.business.model.flow.WebAuthorizationFlow
import com.sympauthy.business.model.oauth2.AuthorizeAttempt
import com.sympauthy.business.model.user.User
import com.sympauthy.security.SecurityRule.HAS_VALID_STATE
import com.sympauthy.security.authorizeAttempt
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.swagger.v3.oas.annotations.Operation
import jakarta.inject.Inject

@Secured(HAS_VALID_STATE)
@Controller("/api/v1/flow/claims/validation")
class ClaimsValidationController(
    @Inject private val authorizationFlowManager: AuthorizationFlowManager,
    @Inject private val claimValidationManager: AuthorizationFlowClaimValidationManager,
    @Inject private val collectedClaimManager: CollectedClaimManager,
    @Inject private val resourceMapper: ClaimsValidationFlowResultResourceMapper,
    @Inject private val redirectUriBuilder: WebAuthorizationFlowRedirectUriBuilder,
    @Inject private val flowControllerHelper: FlowControllerHelper
) {

    @Operation(
        method = "Send validation codes to collect",
        description = """
Send the codes to validate the claims populated by the user earlier using their associated media.
Then return the list of validation codes the authorization server expect from the user.
ex. this authorization server will send an email to the email claim of the user to validate it has access to the box.

If there is no more validation codes expected by the authorization server, it will respond with a redirect url
to continue the authorization flow.

To avoid spamming the user, if a validation code has already been sent to the user for a given claim,
the code will not be sent again but it will still be present in the output.

The dedicated operation "Resend claim validation code" allows the user to ask for another code in case they did not
receive the previous one.
""",
        tags = ["flow"]
    )
    @Get
    suspend fun sendValidationCodesToCollect(
        authentication: Authentication
    ): ClaimsValidationFlowResultResource {
        val authorizeAttempt = authentication.authorizeAttempt
        val user = flowControllerHelper.getUser(authentication)
        val flow = authorizationFlowManager.verifyIsWebFlow(authorizeAttempt)
        return mapToResultResource(
            authorizeAttempt = authorizeAttempt,
            user = user,
            flow = flow
        )
    }

    private suspend fun mapToResultResource(
        authorizeAttempt: AuthorizeAttempt,
        user: User,
        flow: WebAuthorizationFlow,
    ): ClaimsValidationFlowResultResource {
        val collectedClaims = collectedClaimManager.findClaimsReadableByAttempt(authorizeAttempt)
        val result = authorizationFlowManager.checkIfAuthorizationIsComplete(
            user = user,
            collectedClaims = collectedClaims,
        )
        return if (result.missingValidation) {
            val validationCodes = claimValidationManager.getOrSendValidationCodes(
                authorizeAttempt = authorizeAttempt,
                user = user
            )
            resourceMapper.toResource(validationCodes)
        } else {
            val redirectUri = redirectUriBuilder.getRedirectUri(
                authorizeAttempt = authorizeAttempt,
                flow = flow,
                result = result
            )
            resourceMapper.toResource(redirectUri)
        }
    }

    @Operation(
        method = "Validate code",
        description = "Validate the code entered by the user.",
        tags = ["flow"]
    )
    @Post
    suspend fun validate(
        authentication: Authentication,
        @Body inputResource: ClaimValidationInputResource
    ): ClaimsValidationFlowResultResource {
        val authorizeAttempt = authentication.authorizeAttempt
        val user = flowControllerHelper.getUser(authentication)
        val flow = authorizationFlowManager.verifyIsWebFlow(authorizeAttempt)

        claimValidationManager.validateClaimsByCode(
            authorizeAttempt = authorizeAttempt,
            media = ValidationCodeMedia.valueOf(inputResource.media),
            code = inputResource.code
        )

        return mapToResultResource(
            authorizeAttempt = authorizeAttempt,
            user = user,
            flow = flow
        )
    }

    @Operation(
        method = "Resend claim validation code",
        description = "Resend a validation code to the user.",
        tags = ["flow"]
    )
    @Post("/resend")
    suspend fun resendValidationCode(
        authentication: Authentication
    ) {
        TODO("FIXME")
    }
}
