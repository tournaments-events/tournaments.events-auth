package tournament.events.auth.data.model

import org.komapper.annotation.KomapperColumn
import org.komapper.annotation.KomapperEntityDef
import org.komapper.annotation.KomapperId
import org.komapper.annotation.KomapperTable
import java.util.*

data class UserEntity(
    override var id: UUID? = null,
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
) : IdentifiableEntity

@KomapperEntityDef(UserEntity::class)
@KomapperTable(name = "users")
data class UserEntityDef(
    @KomapperId val id: Nothing,
    val username: Nothing,
    val firstname: Nothing,
    val lastname: Nothing,
    val email: Nothing,
    val password: Nothing,
    @KomapperColumn("is_admin") val admin: Nothing
)

