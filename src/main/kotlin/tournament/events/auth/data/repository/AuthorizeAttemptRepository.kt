package tournament.events.auth.data.repository

import jakarta.inject.Singleton
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.r2dbc.R2dbcDatabase
import tournament.events.auth.data.model.*
import java.util.UUID

@Singleton
class AuthorizeAttemptRepository(
    database: R2dbcDatabase
): AbstractRepository<AuthorizeAttemptEntity, UUID, _AuthorizeAttemptEntityDef>(
    database,
    Meta.authorizeAttemptEntity
) {

    suspend fun findByClientState(state: String): AuthorizeAttemptEntity? = findOne {
        QueryDsl.from(it).where { it.clientState eq state }
    }
}
