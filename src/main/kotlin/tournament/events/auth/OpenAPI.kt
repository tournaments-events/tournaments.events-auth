package tournament.events.auth

import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.context.event.StartupEvent
import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn.HEADER
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType.OAUTH2
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License
import io.swagger.v3.oas.annotations.security.*
import io.swagger.v3.oas.annotations.servers.Server
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.config.model.UrlsConfig
import tournament.events.auth.config.model.orThrow
import tournament.events.auth.util.loggerForClass

@OpenAPIDefinition(
    info = Info(
        title = "SympAuthy",
        version = "1.0",
        description = "An open-source authentication, authorization and identification server.",
        license = License(
            name = "GNU General Public License v3.0",
            url = "https://www.gnu.org/licenses/gpl-3.0.fr.html#license-text"
        )
    ),
    servers = [
        Server(
            description = "Instance of SympAuthy serving this documentation.",
            url = "{rootUrl}"
        )
    ],
    tags = [
        Tag(
            name = "oauth2",
            description = "OAuth 2.0",
            externalDocs = ExternalDocumentation(
                url = "https://datatracker.ietf.org/doc/html/rfc6749"
            )
        ),
        Tag(
            name = "openid",
            description = "OpenID Connect Core 1.0",
            externalDocs = ExternalDocumentation(
                url = "https://openid.net/specs/openid-connect-core-1_0.html"
            )
        ),
        Tag(
            name = "openiddiscovery",
            description = "OpenID Connect Discovery 1.0",
            externalDocs = ExternalDocumentation(
                url = "https://openid.net/specs/openid-connect-discovery-1_0.html"
            )
        )
    ]
)
@SecuritySchemes(
    value = [
        SecurityScheme(
            name = "SympAuthy",
            description = "Authenticated to this authentication server to access its protected resources.",
            type = OAUTH2,
            `in` = HEADER,
            flows = OAuthFlows(
                authorizationCode = OAuthFlow(
                    authorizationUrl = "{rootUrl}/api/oauth2/authorize",
                    tokenUrl = "{rootUrl}/api/oauth2/token",
                    refreshUrl = "{rootUrl}/api/oauth2/token",
                    scopes = [
                        OAuthScope(name = "profile")
                    ]
                )
            )
        )
    ]
)
@Singleton
class OpenAPI(
    @Inject private val urlsConfig: UrlsConfig
) : ApplicationEventListener<StartupEvent> {

    private val log = loggerForClass()

    override fun onApplicationEvent(event: StartupEvent) {
        log.info("OpenAPI documentation available at: ${urlsConfig.orThrow().root}/openapi.yml")
        log.info("Swagger UI available at: ${urlsConfig.orThrow().root}/swagger-ui")
    }
}
