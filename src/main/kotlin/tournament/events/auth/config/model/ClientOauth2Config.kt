package tournament.events.auth.config.model

import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class ClientOauth2Config(
    var clientId: String?,
    var clientSecret: String?,
    var scopes: List<String>?,
    var authorizationUrl: String?
)
