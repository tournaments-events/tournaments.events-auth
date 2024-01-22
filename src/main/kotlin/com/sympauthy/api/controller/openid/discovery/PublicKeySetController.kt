package com.sympauthy.api.controller.openid.discovery

import com.sympauthy.api.controller.openid.discovery.PublicKeySetController.Companion.OPENID_JWKS_ENDPOINT
import com.sympauthy.business.manager.jwt.JwtManager
import io.micronaut.http.MediaType.APPLICATION_JSON
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS
import io.swagger.v3.oas.annotations.Operation
import jakarta.inject.Inject

@Secured(IS_ANONYMOUS)
@Controller(OPENID_JWKS_ENDPOINT)
class PublicKeySetController(
    @Inject private val jwtManager: JwtManager
) {

    @Operation(
        description = """
Return a key set containing the public key to use to validate access tokens and id tokens issued by this authorization server.
""",
        tags = ["openiddiscovery"]
    )
    @Get(produces = ["application/jwk-set+json", APPLICATION_JSON])
    suspend fun getPublicKey(): Map<String, Any> {
        return jwtManager.getPublicKeySet().toJSONObject()
    }

    companion object {
        const val OPENID_JWKS_ENDPOINT = "/.well-known/public.jwk"
    }
}
