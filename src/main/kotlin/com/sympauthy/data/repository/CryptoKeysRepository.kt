package com.sympauthy.data.repository

import com.sympauthy.data.model.CryptoKeysEntity
import io.micronaut.data.model.query.builder.sql.Dialect.POSTGRES
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository

@R2dbcRepository(dialect = POSTGRES)
interface CryptoKeysRepository : CoroutineCrudRepository<CryptoKeysEntity, String> {

    suspend fun findByName(name: String): CryptoKeysEntity?
}
