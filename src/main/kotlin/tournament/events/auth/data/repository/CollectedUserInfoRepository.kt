package tournament.events.auth.data.repository

import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.jpa.kotlin.CoroutineJpaSpecificationExecutor
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import io.micronaut.data.runtime.criteria.get
import io.micronaut.data.runtime.criteria.where
import tournament.events.auth.business.model.user.StandardClaim
import tournament.events.auth.data.model.CollectedUserInfoEntity
import java.util.*

@R2dbcRepository(dialect = Dialect.POSTGRES)
interface CollectedUserInfoRepository : CoroutineCrudRepository<CollectedUserInfoEntity, UUID>,
    CoroutineJpaSpecificationExecutor<CollectedUserInfoEntity>

suspend fun CollectedUserInfoRepository.findByLogin(login: String, loginClaims: List<StandardClaim>): CollectedUserInfoEntity? {
    return findOne(where {
        or {
            loginClaims.forEach {
                when (it) {
                    StandardClaim.PREFERRED_USERNAME -> root[CollectedUserInfoEntity::preferredUsername] eq login
                    StandardClaim.EMAIL -> root[CollectedUserInfoEntity::email] eq login
                    StandardClaim.PHONE_NUMBER -> root[CollectedUserInfoEntity::phoneNumber] eq login
                    else -> throw IllegalArgumentException("Unsupported login claim $it.")
                }
            }
        }
    })
}
