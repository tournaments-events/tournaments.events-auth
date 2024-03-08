package com.sympauthy.data.repository

import com.sympauthy.data.model.AuthorizationCodeEntity
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

@R2dbcRepository(dialect = Dialect.POSTGRES)
interface AuthorizationCodeRepository : CoroutineCrudRepository<AuthorizationCodeEntity, UUID> {

    suspend fun findByCode(code: String): AuthorizationCodeEntity?

    suspend fun deleteByCode(code: String)
}
