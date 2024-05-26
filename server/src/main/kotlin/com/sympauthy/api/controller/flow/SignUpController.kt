package com.sympauthy.api.controller.flow

import com.sympauthy.api.mapper.CollectedClaimUpdateMapper
import com.sympauthy.api.resource.flow.FlowResultResource
import com.sympauthy.api.resource.flow.SignUpInputResource
import com.sympauthy.business.manager.flow.AuthorizationFlowManager
import com.sympauthy.business.manager.flow.PasswordFlowManager
import com.sympauthy.business.manager.flow.WebAuthorizationFlowRedirectUriBuilder
import com.sympauthy.security.SecurityRule.HAS_VALID_STATE
import com.sympauthy.security.authorizeAttempt
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.swagger.v3.oas.annotations.Operation
import jakarta.inject.Inject

@Secured(HAS_VALID_STATE)
@Controller("/api/v1/flow/sign-up")
class SignUpController(
    @Inject private val authorizationFlowManager: AuthorizationFlowManager,
    @Inject private val passwordFlowManager: PasswordFlowManager,
    @Inject private val redirectUriBuilder: WebAuthorizationFlowRedirectUriBuilder,
    @Inject private val collectedClaimUpdateMapper: CollectedClaimUpdateMapper
) {

    @Operation(
        description = """
Initiate the creation of an account of a end-user with a password.
        """,
        tags = ["flow"]
    )
    @Post
    suspend fun signUp(
        authentication: Authentication,
        @Body inputResource: SignUpInputResource
    ): FlowResultResource {
        val authorizeAttempt = authentication.authorizeAttempt
        val flow = authorizationFlowManager.verifyIsWebFlow(authorizeAttempt)

        val updates = collectedClaimUpdateMapper.toUpdates(inputResource.claims)
        val result = passwordFlowManager.signUpWithClaimsAndPassword(
            authorizeAttempt = authorizeAttempt,
            unfilteredUpdates = updates,
            password = inputResource.password
        )
        return redirectUriBuilder.getRedirectUri(
            authorizeAttempt = authorizeAttempt,
            flow = flow,
            result = result
        ).toString().let(::FlowResultResource)
    }
}
