package tournament.events.auth.api.controller.openid.discovery

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS
import io.swagger.v3.oas.annotations.Operation
import jakarta.inject.Inject
import tournament.events.auth.api.model.openid.OpenIdConfigurationResource
import tournament.events.auth.business.model.user.StandardClaim
import tournament.events.auth.business.model.user.StandardScope
import tournament.events.auth.config.model.*

@Secured(IS_ANONYMOUS)
@Controller("/.well-known/openid-configuration")
class OpenIdConfigurationController(
    @Inject private val authConfig: AuthConfig,
    @Inject private val urlsConfig: UrlsConfig
) {

    @Operation(
        description = "Return the configuration of this OpenID provider.",
        tags = ["openiddiscovery"]
    )
    @Get
    fun getConfiguration(): OpenIdConfigurationResource {
        val enabledAuthConfig = authConfig.orThrow()
        return OpenIdConfigurationResource(
            issuer = enabledAuthConfig.issuer,
            authorizationEndpoint = urlsConfig.authorizeUri.toString(),
            tokenEndpoint = urlsConfig.tokenUri.toString(),
            userInfoEndpoint = urlsConfig.userInfoUri.toString(),
            jwksUri = urlsConfig.jwtUri.toString(),
            scopesSupported = StandardScope.values().map(StandardScope::id),
            responseTypesSupported = listOf("code", "id_token", "token id_token"),
            grantTypesSupported = listOf("authorization_code", "refresh_token"),
            subjectTypesSupported = listOf("public"),
            idTokenSigningAlgValuesSupported = listOf("RS256"),
            tokenEndpointAuthMethodsSupported = listOf("client_secret_basic"),
            claimsSupported = StandardClaim.values().map(StandardClaim::id)
        )
    }
}
