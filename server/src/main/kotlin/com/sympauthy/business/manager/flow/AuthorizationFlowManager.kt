package com.sympauthy.business.manager.flow

import com.sympauthy.business.manager.user.CollectedClaimManager
import com.sympauthy.business.model.flow.AuthorizationFlow
import com.sympauthy.business.model.flow.AuthorizationFlow.Companion.DEFAULT_AUTHORIZATION_FLOW_ID
import com.sympauthy.business.model.flow.WebAuthorizationFlow
import com.sympauthy.business.model.oauth2.AuthorizeAttempt
import com.sympauthy.business.model.user.CollectedClaim
import com.sympauthy.business.model.user.User
import com.sympauthy.config.model.AuthorizationFlowsConfig
import com.sympauthy.config.model.UrlsConfig
import com.sympauthy.config.model.orThrow
import com.sympauthy.view.DefaultAuthorizationFlowController.Companion.USER_FLOW_ENDPOINT
import io.micronaut.http.uri.UriBuilder
import jakarta.inject.Inject
import jakarta.inject.Singleton

/**
 * Manager in charge of checking if the authorization flow of a user is completed.
 */
@Singleton
class AuthorizationFlowManager(
    @Inject private val collectedClaimManager: CollectedClaimManager,
    @Inject private val claimValidationManager: AuthorizationFlowClaimValidationManager,
    @Inject private val authorizationFlowsConfig: AuthorizationFlowsConfig,
    @Inject private val uncheckedUrlsConfig: UrlsConfig
) {

    /**
     * The default web authentication flow is hardcoded since it is bundled with this authorization server.
     */
    val defaultAuthorizationFlow: WebAuthorizationFlow by lazy {
        val builder = uncheckedUrlsConfig.orThrow().root
            .let(UriBuilder::of)
            .path(USER_FLOW_ENDPOINT)
        WebAuthorizationFlow(
            id = DEFAULT_AUTHORIZATION_FLOW_ID,
            signInUri = builder.path("sign-in").build(),
            collectClaimsUri = builder.path("claims/edit").build(),
            validateClaimsUri = builder.path("claims/validate").build(),
            errorUri = builder.path("error").build(),
        )
    }

    /**
     * Return the [AuthorizationFlow] identified by [id] or null.
     */
    fun findById(id: String): AuthorizationFlow? {
        if (id == DEFAULT_AUTHORIZATION_FLOW_ID) {
            return defaultAuthorizationFlow
        }
        return authorizationFlowsConfig.orThrow().flows
            .firstOrNull { it.id == id }
    }

    /**
     * Verify the authorization flow used by the [authorizeAttempt] is a [WebAuthorizationFlow].
     */
    fun verifyIsWebFlow(
        authorizeAttempt: AuthorizeAttempt
    ): WebAuthorizationFlow {
        val flow = authorizeAttempt.authorizationFlowId?.let(this::findById)
        if (flow !is WebAuthorizationFlow) {
            TODO("Put proper exception to verify the flow")
        }
        return flow
    }

    suspend fun checkIfAuthorizationIsComplete(
        user: User,
        collectedClaims: List<CollectedClaim>
    ): AuthorizationFlowResult {
        val missingRequiredClaims = !collectedClaimManager.areAllRequiredClaimCollected(collectedClaims)
        val missingValidation = claimValidationManager.getReasonsToSendValidationCode(collectedClaims).isNotEmpty()

        return AuthorizationFlowResult(
            user = user,
            missingRequiredClaims = missingRequiredClaims,
            missingValidation = missingValidation
        )
    }
}

/**
 * The result of the authorization flow.
 */
data class AuthorizationFlowResult(
    /**
     * The user that has been authentication during the authentication flow.
     */
    val user: User,
    /**
     * True if we are missing some required claims from the end-user and they must be collected by the client.
     */
    val missingRequiredClaims: Boolean,
    /**
     * True if some claims requires a validation by the end-user.
     */
    val missingValidation: Boolean,
) {

    /**
     * True if the authorization is complete and the user can be redirected to the client.
     */
    val complete: Boolean = listOf(
        missingRequiredClaims,
        missingValidation
    ).none { it }
}
