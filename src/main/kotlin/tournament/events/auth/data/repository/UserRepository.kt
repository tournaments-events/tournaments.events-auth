package tournament.events.auth.data.repository

import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.r2dbc.R2dbcDatabase
import tournament.events.auth.data.model.UserEntity
import tournament.events.auth.data.model._UserEntityDef
import tournament.events.auth.data.model.userEntity
import java.util.*

class UserRepository(
    database: R2dbcDatabase
) : AbstractRepository<UserEntity, UUID, _UserEntityDef>(
    database,
    Meta.userEntity
) {

    fun findByEmail(email: String) = findOne {
        QueryDsl.from(it).where { it.email eq email }
    }
}
