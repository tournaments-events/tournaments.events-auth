package com.sympauthy.data.postgresql.repository

import com.sympauthy.data.repository.ProviderUserInfoRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository

@Suppress("unused")
@R2dbcRepository(dialect = Dialect.POSTGRES)
interface PostgreSQLProviderUserInfoRepository : ProviderUserInfoRepository
