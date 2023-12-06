package tournament.events.auth.data.repository

import io.micronaut.data.model.query.builder.sql.Dialect.POSTGRES
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import tournament.events.auth.data.model.JwtKeysEntity

@R2dbcRepository(dialect = POSTGRES)
interface JwtKeysRepository : CoroutineCrudRepository<JwtKeysEntity, String> {

    suspend fun findByName(name: String): JwtKeysEntity?
}
