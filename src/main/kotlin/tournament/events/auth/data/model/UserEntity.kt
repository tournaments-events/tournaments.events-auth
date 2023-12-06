package tournament.events.auth.data.model

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.serde.annotation.Serdeable
import java.util.*

@Serdeable
@MappedEntity("users")
data class UserEntity(
    val username: String,

    val firstname: String? = null,
    val lastname: String? = null,
    val email: String,

    /**
     * Password is only saved for user having created their account without using a third-party provider.
     * The password is stored hashed for security measure.
     */
    val password: String? = null,

    val admin: Boolean = false
) {
    @Id @GeneratedValue
    var id: UUID? = null
}

