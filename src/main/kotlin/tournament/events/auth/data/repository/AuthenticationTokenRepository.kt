package tournament.events.auth.data.repository

import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import tournament.events.auth.data.model.AuthenticationTokenEntity
import java.util.*

@R2dbcRepository(dialect = Dialect.POSTGRES)
interface AuthenticationTokenRepository : CoroutineCrudRepository<AuthenticationTokenEntity, UUID> {

    fun updateRevokedById(id: UUID, revoked: Boolean)
}
