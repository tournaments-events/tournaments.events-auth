package com.sympauthy.security

import com.sympauthy.business.model.oauth2.AuthenticationToken
import com.sympauthy.exception.httpExceptionOf
import io.micronaut.http.HttpStatus.FORBIDDEN
import io.micronaut.security.authentication.Authentication
import java.util.*

/**
 * Represent the state of authentication of a user.
 */
class UserAuthentication(
    /**
     * The token used by the end-user to authorize its request.
     */
    val authorizationToken: AuthenticationToken
): Authentication {

    override fun getName(): String = authorizationToken.userId.toString()

    override fun getAttributes(): Map<String, Any> = emptyMap()

    override fun getRoles(): Collection<String> = listOf("ROLE_USER")
}

/**
 * Downcast the [Authentication] to a [UserAuthentication].
 * Throws a [FORBIDDEN] if the downcast is not possible meaning the authentication does not contains a user.
 */
val Authentication.userAuthentication: UserAuthentication
    get() = when(this) {
        is UserAuthentication -> this
        else -> throw httpExceptionOf(FORBIDDEN, "authentication.wrong")
    }

val Authentication.scopeTokens: List<String>
    get() = this.userAuthentication.authorizationToken.scopeTokens

val Authentication.userId: UUID
    get() = this.userAuthentication.authorizationToken.userId

