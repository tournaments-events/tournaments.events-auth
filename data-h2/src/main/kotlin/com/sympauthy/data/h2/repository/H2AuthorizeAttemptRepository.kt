package com.sympauthy.data.h2.repository

import com.sympauthy.data.repository.AuthorizeAttemptRepository
import io.micronaut.data.model.query.builder.sql.Dialect.H2
import io.micronaut.data.r2dbc.annotation.R2dbcRepository

@Suppress("unused")
@R2dbcRepository(dialect = H2)
interface H2AuthorizeAttemptRepository : AuthorizeAttemptRepository
