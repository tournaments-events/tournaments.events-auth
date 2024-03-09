package com.sympauthy.security

import com.sympauthy.api.exception.oauth2ExceptionOf
import com.sympauthy.business.model.client.Client
import com.sympauthy.business.model.oauth2.OAuth2ErrorCode.ACCESS_DENIED
import io.micronaut.security.authentication.Authentication

class ClientAuthentication(
    val client: Client
) : Authentication {
    override fun getName(): String = client.id

    override fun getRoles(): Collection<String> {
        return listOf("ROLE_CLIENT")
    }

    override fun getAttributes(): Map<String, Any> {
        return emptyMap()
    }
}

val Authentication.clientAuthentication: ClientAuthentication
    get() = when (this) {
        is ClientAuthentication -> this
        else -> throw oauth2ExceptionOf(ACCESS_DENIED, "authentication.wrong")
    }

val Authentication.client: Client
    get() = clientAuthentication.client
