package tournament.events.auth.config.model

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("jwt")
class JwtConfig {
    var algorithm: String? = null
    var issuer: String? = null
}
