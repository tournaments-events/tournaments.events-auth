package com.sympauthy.security

import com.sympauthy.business.model.oauth2.AuthorizeAttempt
import com.sympauthy.exception.httpExceptionOf
import com.sympauthy.security.SecurityRule.HAS_VALID_STATE
import io.micronaut.http.HttpStatus.FORBIDDEN
import io.micronaut.security.authentication.Authentication

class StateAuthentication(
    val authorizeAttempt: AuthorizeAttempt
) : Authentication {
    override fun getName(): String = authorizeAttempt.id.toString()

    override fun getRoles(): Collection<String> {
        return listOf(HAS_VALID_STATE)
    }

    override fun getAttributes(): Map<String, Any> {
        return emptyMap()
    }
}

val Authentication.stateAuthentication: StateAuthentication
    get() = when (this) {
        is StateAuthentication -> this
        else -> throw httpExceptionOf(FORBIDDEN, "authentication.wrong")
    }

val Authentication.authorizeAttempt: AuthorizeAttempt
    get() = stateAuthentication.authorizeAttempt
