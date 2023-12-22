package tournament.events.auth.business.model.auth.oauth2

import java.time.LocalDateTime
import java.util.*

data class EncodedAuthenticationToken(
    val id: UUID,
    val type: AuthenticationTokenType,
    val token: String,
    val issueDate: LocalDateTime,
    val expirationDate: LocalDateTime?
)
