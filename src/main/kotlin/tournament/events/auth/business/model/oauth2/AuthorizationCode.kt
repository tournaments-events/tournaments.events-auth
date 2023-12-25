package tournament.events.auth.business.model.oauth2

import java.time.LocalDateTime
import java.util.UUID

data class AuthorizationCode(
    val attemptId: UUID,
    val code: String,
    val creationDate: LocalDateTime,
    val expirationDate: LocalDateTime
)
