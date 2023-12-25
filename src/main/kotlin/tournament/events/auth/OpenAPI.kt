package tournament.events.auth

import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.context.event.StartupEvent
import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.inject.Singleton
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
    tags = [
        Tag(
            name = "OAuth 2.0",
            externalDocs = ExternalDocumentation(
                url = "https://datatracker.ietf.org/doc/html/rfc6749"
            )
        ),
        Tag(
            name = "OpenID Connect Core 1.0",
            externalDocs = ExternalDocumentation(
                url = "https://openid.net/specs/openid-connect-core-1_0.html"
            )
        ),
        Tag(
            name = "OpenID Connect Discovery 1.0",
            externalDocs = ExternalDocumentation(
                url = "https://openid.net/specs/openid-connect-discovery-1_0.html"
            )
        )
    ]
)
@Singleton
class OpenAPI: ApplicationEventListener<StartupEvent> {

    private val log = loggerForClass()

    override fun onApplicationEvent(event: StartupEvent) {
        OpenAPI::class
    }
}
