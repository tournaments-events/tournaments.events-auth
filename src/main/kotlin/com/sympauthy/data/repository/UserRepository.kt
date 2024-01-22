package com.sympauthy.data.repository

import com.sympauthy.data.model.UserEntity
import io.micronaut.data.model.query.builder.sql.Dialect.POSTGRES
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

@R2dbcRepository(dialect = POSTGRES)
interface UserRepository : CoroutineCrudRepository<UserEntity, UUID>
