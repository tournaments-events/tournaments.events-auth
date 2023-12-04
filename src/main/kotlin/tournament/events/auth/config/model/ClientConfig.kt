package tournament.events.auth.config.model

import io.micronaut.context.annotation.EachProperty
import io.micronaut.context.annotation.Parameter
import io.micronaut.serde.annotation.Serdeable

/**
 * Configuration of an external authentication provider (ex. Discord) that can be used to
 * create an account on tournaments.events.
 */
@Serdeable
@EachProperty("clients")
class ClientConfig(
    @param:Parameter val id: String
) {
    /**
     * Display name of the client.
     */
    var name: String? = null

    /**
     * Configuration of display elements related to the client.
     * ex. url of the icon, colors used to display the client.
     */
    var ui: ClientUIConfig? = null

    /**
     * To be used the client must support the authorization code grant type
     */
    val oauth: ClientOauth2Config? = null
}
