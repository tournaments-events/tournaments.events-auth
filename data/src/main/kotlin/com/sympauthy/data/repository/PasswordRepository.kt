package com.sympauthy.data.repository

import com.sympauthy.data.model.PasswordEntity
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

interface PasswordRepository : CoroutineCrudRepository<PasswordEntity, UUID> {

    suspend fun findByUserId(userId: UUID): List<PasswordEntity>
}
