package tournament.events.auth.data.repository

import jakarta.inject.Singleton
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.r2dbc.R2dbcDatabase
import tournament.events.auth.data.model.LoginAttemptEntity
import tournament.events.auth.data.model._LoginAttemptEntityDef
import tournament.events.auth.data.model.loginAttemptEntity

@Singleton
class LoginAttemptRepository(
    database: R2dbcDatabase
): AbstractRepository<LoginAttemptEntity, String, _LoginAttemptEntityDef>(
    database,
    Meta.loginAttemptEntity
) {

    fun findByState(state: String) = findOne {
        QueryDsl.from(it).where { it.state eq state }
    }
}
