package com.sympauthy.api.util.flow

import com.sympauthy.api.util.orBadRequest
import com.sympauthy.business.manager.user.UserManager
import com.sympauthy.business.model.oauth2.AuthorizeAttempt
import com.sympauthy.business.model.user.User
import com.sympauthy.security.authorizeAttempt
import io.micronaut.security.authentication.Authentication
import jakarta.inject.Inject
import jakarta.inject.Singleton

/**
 * Utility methods shared between all the controllers of the authentication flow.
 */
@Singleton
class FlowControllerHelper(
    @Inject private val userManager: UserManager
) {

    suspend fun getUser(authentication: Authentication): User {
        return getUser(authentication.authorizeAttempt)
    }

    suspend fun getUser(authorizeAttempt: AuthorizeAttempt): User {
        val userId = authorizeAttempt.userId.orBadRequest("flow.user.missing")
        return userManager.findById(userId) ?: throw IllegalStateException("No user found with id $userId")
    }
}
