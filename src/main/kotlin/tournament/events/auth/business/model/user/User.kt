package tournament.events.auth.business.model.user

import java.time.LocalDateTime
import java.util.UUID

data class User(
    val id: UUID,

    val email: String?,

    val creationDate: LocalDateTime
)
