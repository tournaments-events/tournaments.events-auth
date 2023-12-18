package tournament.events.auth.business.model.auth.oauth2

import java.util.UUID

data class AuthorizationCode(
    val attemptId: UUID,
    val code: String
)
