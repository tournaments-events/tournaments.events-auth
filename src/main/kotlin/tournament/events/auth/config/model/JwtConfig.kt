package tournament.events.auth.config.model

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("jwt")
interface JwtConfig {
    val algorithm: String?
    val issuer: String?
}
