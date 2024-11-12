package com.sympauthy.api.controller.flow

import com.sympauthy.api.mapper.flow.ClaimsValidationFlowResultResourceMapper
import com.sympauthy.api.resource.flow.*
import com.sympauthy.api.util.flow.FlowControllerHelper
import com.sympauthy.business.manager.flow.AuthorizationFlowClaimValidationManager
import com.sympauthy.business.manager.flow.AuthorizationFlowManager
import com.sympauthy.business.manager.flow.WebAuthorizationFlowRedirectUriBuilder
import com.sympauthy.business.manager.user.CollectedClaimManager
import com.sympauthy.business.model.code.ValidationCodeMedia
import com.sympauthy.security.SecurityRule.HAS_VALID_STATE
import com.sympauthy.security.authorizeAttempt
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
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
        method = "Send validation code to collect for media",
        description = """
Send the codes to validate the claims populated by the user earlier using their associated media.
Then return the list of validation codes the authorization server expect from the user.
ex. this authorization server will send an email to the email claim of the user to validate it has access to the box.

If there is no more validation code expected by the authorization server for this media, 
the response will contain the redirect uri where the user must be redirected to continue the authorization flow.

To avoid spamming the user, if a validation code has already been sent to the user for the media,
the code will not be sent again but it will still be present in the output.

The dedicated operation "Resend claim validation code" allows the user to ask for another code in case they did not
receive the previous one.
""",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = ""
            ),
            ApiResponse(
                responseCode = "204",
                description = "No validation code required for this media."
            )
        ],
        tags = ["flow"]
    )
    @Get("/{media}")
    suspend fun getValidationCodeToCollectForMedia(
        authentication: Authentication,
        media: ValidationCodeMedia,
    ): SendValidationCodeOrGetFlowResultResource {
        val authorizeAttempt = authentication.authorizeAttempt
        val user = flowControllerHelper.getUser(authentication)
        val flow = authorizationFlowManager.verifyIsWebFlow(authorizeAttempt)

        val collectedClaims = collectedClaimManager.findClaimsReadableByAttempt(authorizeAttempt)
        val result = authorizationFlowManager.checkIfAuthorizationIsComplete(
            user = user,
            collectedClaims = collectedClaims,
        )

        val validationCode = claimValidationManager.getOrSendValidationCode(
            authorizeAttempt = authorizeAttempt,
            user = user,
            media = media
        )

        return if (validationCode != null) {
            resourceMapper.toFlowResultResource(validationCode)
        } else {
            val redirectUri = redirectUriBuilder.getRedirectUri(
                authorizeAttempt = authorizeAttempt,
                flow = flow,
                result = result
            )
            resourceMapper.toFlowResultResource(redirectUri)
        }
    }

    fun getMedia(media: String): ValidationCodeMedia {
        return ValidationCodeMedia.valueOf(media)
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
    ): FlowResultResource {
        val authorizeAttempt = authentication.authorizeAttempt
        val user = flowControllerHelper.getUser(authentication)
        val flow = authorizationFlowManager.verifyIsWebFlow(authorizeAttempt)

        claimValidationManager.validateClaimsByCode(
            authorizeAttempt = authorizeAttempt,
            media = ValidationCodeMedia.valueOf(inputResource.media),
            code = inputResource.code
        )

        val collectedClaims = collectedClaimManager.findClaimsReadableByAttempt(authorizeAttempt)
        val result = authorizationFlowManager.checkIfAuthorizationIsComplete(
            user = user,
            collectedClaims = collectedClaims,
        )

        val redirectUri = redirectUriBuilder.getRedirectUri(
            authorizeAttempt = authorizeAttempt,
            flow = flow,
            result = result
        )
        return FlowResultResource(redirectUri.toString())
    }

    @Operation(
        method = "Resend claim validation code",
        description =
        """
Ask this authorization server to send through the media in the body new codes 
to validates the claims populated by the user earlier.

This authorization server will not send new validation code in the following cases:
- To avoid spamming.
- The claims have already been validated.
- No claim require validation through this media.
""",
        tags = ["flow"]
    )
    @Post("/resend")
    suspend fun resendValidationCodes(
        authentication: Authentication,
        @Body inputResource: ResendClaimsValidationInputResource
    ): ResendClaimsValidationCodesResultResource {
        val authorizeAttempt = authentication.authorizeAttempt
        val user = flowControllerHelper.getUser(authentication)
        val media = getMedia(inputResource.media)

        val result = claimValidationManager.resendValidationCode(
            authorizeAttempt = authorizeAttempt,
            user = user,
            media = media
        )

        return resourceMapper.toResendResultResource(
            media = media,
            resent = result.resent,
            newValidationCode = if (result.resent) result.validationCode else null,
        )
    }
}
