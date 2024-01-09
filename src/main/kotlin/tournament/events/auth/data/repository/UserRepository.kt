package tournament.events.auth.data.repository

import io.micronaut.data.annotation.Query
import io.micronaut.data.model.query.builder.sql.Dialect.POSTGRES
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import tournament.events.auth.data.model.UserEntity
import java.util.*

@R2dbcRepository(dialect = POSTGRES)
interface UserRepository : CoroutineCrudRepository<UserEntity, UUID> {

    @Query("""
        SELECT u.* FROM users AS u
        JOIN collected_user_info AS cui ON u.id = cui.user_id 
        WHERE cui.email = :email
    """)
    suspend fun findByEmail(email: String): UserEntity?
}
