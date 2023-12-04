package tournament.events.auth.data.model

import org.komapper.annotation.*
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.util.*

/**
 * This entity store all information related to an user trying to authenticate through this service.
 */
data class AuthorizeAttemptEntity(
    override var id: UUID? = null,
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
) : IdentifiableEntity

@Suppress("unused")
@KomapperEntityDef(AuthorizeAttemptEntity::class)
@KomapperTable("authorize_attempts")
data class AuthorizeAttemptEntityDef(
    @KomapperId val id: Nothing,
    @KomapperColumn("client_id") val clientId: Nothing,
    @KomapperColumn("redirect_uri") val redirectUri: Nothing,

    @KomapperColumn("client_ip") val clientIp: Nothing,
    @KomapperColumn("client_user_agent") val clientUserAgent: Nothing,
    @KomapperColumn("client_referer") val clientReferer: Nothing,

    @KomapperColumn("client_state") val clientState: Nothing,

    @KomapperCreatedAt @KomapperColumn("attempt_date") val attemptDate: Nothing
)
