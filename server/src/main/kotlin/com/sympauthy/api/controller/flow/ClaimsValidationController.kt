package com.sympauthy.api.controller.flow

import com.sympauthy.api.mapper.flow.ValidationCodesResourceMapper
import com.sympauthy.api.resource.flow.ClaimsValidationResource
import com.sympauthy.api.util.flow.FlowControllerHelper
import com.sympauthy.business.manager.flow.AuthorizationFlowClaimValidationManager
import com.sympauthy.security.SecurityRule.HAS_VALID_STATE
import com.sympauthy.security.authorizeAttempt
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.swagger.v3.oas.annotations.Operation
import jakarta.inject.Inject

@Secured(HAS_VALID_STATE)
@Controller("/api/v1/flow/claims/validation-codes")
class ClaimsValidationController(
    @Inject private val claimValidationManager: AuthorizationFlowClaimValidationManager,
    @Inject private val resourceMapper: ValidationCodesResourceMapper,
    @Inject private val flowControllerHelper: FlowControllerHelper
) {

    @Operation(
        method = "Send validation codes to collect",
        description = """
Send the codes to validate the claims populated by the user earlier using their associated media.
Then return the list of validation codes the authorization server expect from the user.
ex. this authorization server will send an email to the email claim of the user to validate it has access to the box.

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
    ): ClaimsValidationResource {
        val authorizeAttempt = authentication.authorizeAttempt
        val user = flowControllerHelper.getUser(authentication)
        val validationCodes = claimValidationManager.getOrSendValidationCodes(
            authorizeAttempt = authorizeAttempt,
            user = user
        )
        return resourceMapper.toResource(validationCodes)
    }

    @Operation(
        method = "Validate code",
        description = "Validate the code entered by the user.",
        tags = ["flow"]
    )
    @Post
    suspend fun validate(
        authentication: Authentication,
    ) {
        TODO("FIXME")
    }

    @Operation(
        method = "Resend claim validation code",
        description = "Resend a validation code to the user.",
        tags = ["flow"]
    )
    @Post("/resend")
    suspend fun resendValidationCode(
        authentication: Authentication,
    ) {
        TODO("FIXME")
    }
}
