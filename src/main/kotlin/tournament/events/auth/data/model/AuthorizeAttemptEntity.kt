package tournament.events.auth.data.model

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.serde.annotation.Serdeable
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.util.*

/**
 * This entity store all information related to an user trying to authenticate through this service.
 */
@Serdeable
@MappedEntity("authorize_attempts")
data class AuthorizeAttemptEntity(
    /**
     * Identifier of the client initiating the authentication.
     */
    val clientId: String,
    /**
     * URI where the user must be redirected when the authorization is completed.
     */
    val redirectUri: String,

    val clientIp: String? = null,
    val clientUserAgent: String? = null,
    val clientReferer: String? = null,

    /**
     * The state provided by the Oauth2/OpenId client initiating the authorization flow.
     */
    val clientState: String? = null,

    val attemptDate: LocalDateTime = now(),
) {
    @Id @GeneratedValue
    var id: UUID? = null
}
