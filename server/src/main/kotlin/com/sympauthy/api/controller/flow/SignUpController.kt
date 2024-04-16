package com.sympauthy.api.controller.flow

import com.sympauthy.api.mapper.CollectedClaimUpdateMapper
import com.sympauthy.api.resource.flow.SignUpInputResource
import com.sympauthy.api.resource.flow.SignUpResultResource
import com.sympauthy.api.util.AuthorizationFlowRedirectUriBuilder
import com.sympauthy.business.manager.password.PasswordFlowManager
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
    @Inject private val passwordFlowManager: PasswordFlowManager,
    @Inject private val authorizationFlowRedirectUriBuilder: AuthorizationFlowRedirectUriBuilder,
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
    ): SignUpResultResource {
        val attempt = authentication.authorizeAttempt
        val updates = collectedClaimUpdateMapper.toUpdates(inputResource.claims)
        val result = passwordFlowManager.signUpWithClaimsAndPassword(
            authorizeAttempt = attempt,
            unfilteredUpdates = updates,
            password = inputResource.password
        )
        return authorizationFlowRedirectUriBuilder.getRedirectUri(attempt, result)
            .toString()
            .let(::SignUpResultResource)
    }
}
