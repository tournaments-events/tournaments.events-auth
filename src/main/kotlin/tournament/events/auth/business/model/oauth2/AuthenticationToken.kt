package tournament.events.auth.business.model.oauth2

import java.time.LocalDateTime
import java.util.*

data class AuthenticationToken(
    val id: UUID,
    val type: AuthenticationTokenType,
    val userId: UUID,
    val clientId: String,
    val scopeTokens: List<String>,

    val issueDate: LocalDateTime,
    val expirationDate: LocalDateTime?
)
