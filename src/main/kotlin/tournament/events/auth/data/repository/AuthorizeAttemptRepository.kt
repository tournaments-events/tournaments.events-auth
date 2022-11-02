package tournament.events.auth.data.repository

import jakarta.inject.Singleton
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.r2dbc.R2dbcDatabase
import tournament.events.auth.data.model.*

@Singleton
class AuthorizeAttemptRepository(
    database: R2dbcDatabase
): AbstractRepository<AuthorizeAttemptEntity, String, _AuthorizeAttemptEntityDef>(
    database,
    Meta.authorizeAttemptEntity
) {

    fun findByState(state: String) = findOne {
        QueryDsl.from(it).where { it.state eq state }
    }
}
