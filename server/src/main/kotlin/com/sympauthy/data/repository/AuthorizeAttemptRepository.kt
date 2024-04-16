package com.sympauthy.data.repository

import com.sympauthy.data.model.AuthorizeAttemptEntity
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.Query
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

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

    suspend fun updateUserIdAndGrantedScopes(@Id id: UUID, userId: UUID, grantedScopes: List<String>?)
}
