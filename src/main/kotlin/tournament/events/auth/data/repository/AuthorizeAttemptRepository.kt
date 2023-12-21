package tournament.events.auth.data.repository

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.Query
import io.micronaut.data.model.query.builder.sql.Dialect.POSTGRES
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import tournament.events.auth.data.model.AuthorizeAttemptEntity
import java.util.*

@R2dbcRepository(dialect = POSTGRES)
interface AuthorizeAttemptRepository : CoroutineCrudRepository<AuthorizeAttemptEntity, UUID> {

    suspend fun findByState(state: String): AuthorizeAttemptEntity?

    @Query(
        """
        SELECT * FROM authorize_attempts AS aa
        JOIN authorization_codes AS ac ON aa.id = ac.attempt_id
        WHERE ac.code = :code
        """
    )
    suspend fun findByCode(code: String): AuthorizeAttemptEntity?

    suspend fun updateUserId(@Id id: UUID, userId: UUID)
}
