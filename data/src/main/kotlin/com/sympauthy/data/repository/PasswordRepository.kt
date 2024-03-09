package com.sympauthy.data.repository

import com.sympauthy.data.model.PasswordEntity
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

@R2dbcRepository(dialect = Dialect.POSTGRES)
interface PasswordRepository : CoroutineCrudRepository<PasswordEntity, UUID> {

    suspend fun findByUserId(userId: UUID): List<PasswordEntity>
}
