package tournament.events.auth.config.model

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("cluster")
class ClusterConfig {
    var keysGenerationStrategy: String? = null
}
