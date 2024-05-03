package com.sympauthy.api.controller.flow

import com.sympauthy.api.exception.httpExceptionOf
import com.sympauthy.api.mapper.CollectedClaimUpdateMapper
import com.sympauthy.api.mapper.flow.ClaimsResourceMapper
import com.sympauthy.api.resource.flow.ClaimInputResource
import com.sympauthy.api.resource.flow.ClaimsResource
import com.sympauthy.api.resource.flow.FlowResultResource
import com.sympauthy.api.util.AuthorizationFlowRedirectUriBuilder
import com.sympauthy.api.util.flow.FlowControllerHelper
import com.sympauthy.business.manager.flow.AuthenticationFlowManager
import com.sympauthy.business.manager.flow.PasswordFlowManager
import com.sympauthy.business.manager.user.CollectedClaimManager
import com.sympauthy.business.model.user.CollectedClaimUpdate
import com.sympauthy.security.SecurityRule.HAS_VALID_STATE
import com.sympauthy.security.authorizeAttempt
import io.micronaut.http.HttpStatus.BAD_REQUEST
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
@Controller("/api/v1/flow/claims")
class ClaimsController(
    @Inject private val collectedClaimManager: CollectedClaimManager,
    @Inject private val passwordFlowManager: PasswordFlowManager,
    @Inject private val authenticationFlowManager: AuthenticationFlowManager,
    @Inject private val claimsMapper: ClaimsResourceMapper,
    @Inject private val collectedClaimUpdateMapper: CollectedClaimUpdateMapper,
    @Inject private val authorizationFlowRedirectUriBuilder: AuthorizationFlowRedirectUriBuilder,
    @Inject private val helper: FlowControllerHelper
) {

    @Operation(
        description = """
List all claims already collected by this authorization server for the end-user signing-in/up through the authorization flow.

It includes claims:
- collected as a first-party. ex. a new required claim has been added forcing the end-user to go through the claim 
collection step of the authorization flow again.
- collected from a provider used by the end-user. In this case, the value may be suggested to the user 
(by auto-filling the input) and the user is free to enter another value before confirming. 
        """,
        tags = ["flow"]
    )
    @Get
    suspend fun getCollectedClaims(
        authentication: Authentication,
    ): ClaimsResource {
        val user = helper.getUser(authentication)
        // FIXME: Implement suggestion from provider
        return claimsMapper.toResource(
            collectedClaims = collectedClaimManager.findReadableUserInfoByUserId(user.id)
        )
    }

    @Operation(
        description = """
Save claims collected from the end-user signing-in/up through the authorization flow.

A claim will be saved if:
- it is collectable.
- it is not part of the sign-up claims.

Null/Empty value can be associated to claim to notify the authentication server that the claim has been presented to
the end-user but it declined to fulfill the value.
        """,
        responses = [
            ApiResponse(
                description = "Claims have been collected. The end-user may be redirected to the next step of the flow.",
                responseCode = "200"
            )
        ],
        tags = ["flow"]
    )
    @Post
    suspend fun collectClaims(
        authentication: Authentication,
        @Body inputResource: ClaimInputResource
    ): FlowResultResource {
        val authorizeAttempt = authentication.authorizeAttempt
        val user = helper.getUser(authorizeAttempt)
        val updates = getUpdates(inputResource)

        val collectedClaims = collectedClaimManager.updateUserInfo(user, updates = updates)
        if (collectedClaimManager.areAllRequiredClaimCollected(collectedClaims)) {
            throw httpExceptionOf(BAD_REQUEST, "flow.claims.missing_required")
        }

        val result = authenticationFlowManager.checkIfAuthenticationIsComplete(
            user = user,
            collectedClaims = collectedClaims
        )
        val redirectUri = authorizationFlowRedirectUriBuilder.getRedirectUri(
            attempt = authorizeAttempt,
            result = result
        )
        return FlowResultResource(redirectUri.toString())
    }

    private fun getUpdates(inputResource: ClaimInputResource): List<CollectedClaimUpdate> {
        val signUpClaims = passwordFlowManager.getSignUpClaims()
        return collectedClaimUpdateMapper.toUpdates(inputResource.claims)
            .filter { it.claim.userInputted }
            .filter { !signUpClaims.contains(it.claim) }
    }
}
