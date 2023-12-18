package tournament.events.auth.business.model.auth

import java.time.LocalDateTime
import java.util.UUID

data class AuthorizeAttempt(
    val id: UUID,
    val clientId: String,
    val redirectUri: String,
    val state: String? = null,
    val attemptDate: LocalDateTime = LocalDateTime.now(),
)
