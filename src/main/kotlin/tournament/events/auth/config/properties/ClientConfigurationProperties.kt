package tournament.events.auth.config.properties

import io.micronaut.context.annotation.EachProperty
import io.micronaut.context.annotation.Parameter

/**
 * Configuration of a client application that will authenticate its users.
 */
@EachProperty("clients")
class ClientConfigurationProperties(
    @param:Parameter val id: String
) {
    var secret: String? = null
}
