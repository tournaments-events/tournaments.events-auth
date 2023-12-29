package tournament.events.auth.api.controller.openid

import com.nimbusds.jose.jwk.JWK
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.inject.Inject
import tournament.events.auth.business.manager.jwt.JwtManager

@Controller("/.well-known/public.jwk")
@Secured(SecurityRule.IS_ANONYMOUS)
class PublicKeyController(
    @Inject private val jwtManager: JwtManager
) {

    @Operation(
        description = "Public signing key used to validate access token issued by this authorization server.",
        tags = ["openiddiscovery"],
        responses = [
            ApiResponse(
                description = "Successful operation"
            )
        ]
    )
    @Get(produces = [JWK.MIME_TYPE])
    fun getPublicKey(): String {
        TODO()
    }
}
