package tournament.events.auth.data.repository

import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import tournament.events.auth.data.model.ProviderUserInfoEntity
import tournament.events.auth.data.model.ProviderUserInfoEntityId
import java.util.UUID

@R2dbcRepository(dialect = Dialect.POSTGRES)
interface ProviderUserInfoRepository : CoroutineCrudRepository<ProviderUserInfoEntity, ProviderUserInfoEntityId> {

    suspend fun findByProviderIdAndSubject(providerId: String, subject: String): ProviderUserInfoEntity?

    suspend fun findByUserId(userId: UUID): List<ProviderUserInfoEntity>
}
