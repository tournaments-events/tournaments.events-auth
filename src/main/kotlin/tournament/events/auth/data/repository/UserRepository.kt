package tournament.events.auth.data.repository

import io.micronaut.data.annotation.Query
import io.micronaut.data.model.query.builder.sql.Dialect.POSTGRES
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import tournament.events.auth.data.model.UserEntity
import java.util.*

@R2dbcRepository(dialect = POSTGRES)
interface UserRepository : CoroutineCrudRepository<UserEntity, UUID> {

    suspend fun findByEmail(email: String): UserEntity?

    @Query(
        """
        SELECT u.* FROM users AS u
        JOIN provider_user_info AS pui ON u.id = pui.user_id
        WHERE pui.provider_id = :providerId AND pui.subject = :subject
    """
    )
    suspend fun findBySubject(providerId: String, subject: String): UserEntity
}
