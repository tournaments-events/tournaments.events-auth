package tournament.events.auth.business.model.user

import java.time.LocalDateTime
import java.util.*

data class User(
    val id: UUID,
    val status: UserStatus,

    val password: String,
    val passwordStatus: UserPasswordStatus,

    val creationDate: LocalDateTime
)
