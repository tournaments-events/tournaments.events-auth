package tournament.events.auth.config.model

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("crypto")
interface CryptoConfig {
    val keysGenerationStrategy: String?
}
