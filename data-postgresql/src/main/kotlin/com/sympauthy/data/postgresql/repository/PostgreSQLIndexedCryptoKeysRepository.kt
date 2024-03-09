package com.sympauthy.data.postgresql.repository

import com.sympauthy.data.repository.IndexedCryptoKeysRepository
import io.micronaut.data.model.query.builder.sql.Dialect.POSTGRES
import io.micronaut.data.r2dbc.annotation.R2dbcRepository

@Suppress("unused")
@R2dbcRepository(dialect = POSTGRES)
interface PostgreSQLIndexedCryptoKeysRepository : IndexedCryptoKeysRepository
