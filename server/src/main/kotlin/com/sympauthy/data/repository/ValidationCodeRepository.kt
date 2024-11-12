package com.sympauthy.data.repository

import com.sympauthy.data.model.ValidationCodeEntity
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

interface ValidationCodeRepository : CoroutineCrudRepository<ValidationCodeEntity, UUID> {

    suspend fun findByAttemptId(attemptId: UUID): List<ValidationCodeEntity>

    suspend fun findByAttemptIdAndMedia(attemptId: UUID, media: String): List<ValidationCodeEntity>

    suspend fun deleteByIds(ids: List<UUID>)

    suspend fun deleteByAttemptIdIn(attemptIds: List<UUID>): Int
}
