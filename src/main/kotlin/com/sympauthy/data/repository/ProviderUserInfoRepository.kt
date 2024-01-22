package com.sympauthy.data.repository

import com.sympauthy.data.model.ProviderUserInfoEntity
import com.sympauthy.data.model.ProviderUserInfoEntityId
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

@R2dbcRepository(dialect = Dialect.POSTGRES)
interface ProviderUserInfoRepository : CoroutineCrudRepository<ProviderUserInfoEntity, ProviderUserInfoEntityId> {

    suspend fun findByProviderIdAndSubject(providerId: String, subject: String): ProviderUserInfoEntity?

    suspend fun findByUserId(userId: UUID): List<ProviderUserInfoEntity>
}
