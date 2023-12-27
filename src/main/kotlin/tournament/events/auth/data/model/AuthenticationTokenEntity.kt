package tournament.events.auth.data.model

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.serde.annotation.Serdeable
import java.time.LocalDateTime
import java.util.*

@Serdeable
@MappedEntity("authentication_tokens")
class AuthenticationTokenEntity(
    val type: String,
    val userId: UUID,
    val clientId: String,
    val scopeTokens: Array<String>,
    /**
     * There is no foreign key
     */
    val authorizeAttemptId: UUID,

    val revoked: Boolean,
    val issueDate: LocalDateTime,
    val expirationDate: LocalDateTime?
) {
    @Id
    @GeneratedValue
    var id: UUID? = null
}
