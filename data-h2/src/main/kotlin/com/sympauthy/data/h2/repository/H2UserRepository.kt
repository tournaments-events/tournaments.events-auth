package com.sympauthy.data.h2.repository

import com.sympauthy.data.h2.DefaultDatasourceIsH2
import com.sympauthy.data.repository.UserRepository
import io.micronaut.context.annotation.Requires
import io.micronaut.data.model.query.builder.sql.Dialect.H2
import io.micronaut.data.r2dbc.annotation.R2dbcRepository

@Suppress("unused")
@Requires(condition = DefaultDatasourceIsH2::class)
@R2dbcRepository(dialect = H2)
interface H2UserRepository : UserRepository
