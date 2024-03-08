package com.sympauthy.business.manager.user

import com.sympauthy.business.manager.ClaimManager
import com.sympauthy.business.mapper.ClaimValueMapper
import com.sympauthy.business.mapper.CollectedClaimMapper
import com.sympauthy.business.mapper.CollectedUserInfoUpdateMapper
import com.sympauthy.business.model.user.CollectedClaim
import com.sympauthy.business.model.user.CollectedClaimUpdate
import com.sympauthy.business.model.user.User
import com.sympauthy.business.model.user.claim.Claim
import com.sympauthy.business.security.Context
import com.sympauthy.data.model.CollectedClaimEntity
import com.sympauthy.data.repository.CollectedClaimRepository
import com.sympauthy.data.repository.findAnyClaimMatching
import com.sympauthy.exception.localizedExceptionOf
import com.sympauthy.util.nullIfBlank
import io.micronaut.transaction.annotation.Transactional
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import java.util.*

@Singleton
open class CollectedClaimManager(
    @Inject private val claimManager: ClaimManager,
    @Inject private val collectedClaimRepository: CollectedClaimRepository,
    @Inject private val claimValueMapper: ClaimValueMapper,
    @Inject private val collectedClaimMapper: CollectedClaimMapper,
    @Inject private val collectedClaimUpdateMapper: CollectedUserInfoUpdateMapper
) {

    /**
     * Find users that are conflicting with the [claims] and on which claim they are conflicting.
     *
     * We check the value against all the claims since the login is a single field.
     * Ex. with ```email``` and ```preferred_username``` as login claims,  We can have theoretically the ```email``` claim
     * of an existing end-user conflicting with the ```preferred_username``` of the new end-user.
     */
    internal suspend fun findConflictingUsers(
        claims: List<Claim>,
        values: List<Pair<Claim, Any>>
    ): List<ConflictingCollectedUserClaims> {
        val entityValues = values.mapNotNull { claimValueMapper.toEntity(it.second) }
        val existingClaimEntities = collectedClaimRepository.findAnyClaimMatching(
            claimIds = claims.map(Claim::id),
            claimValues = entityValues
        )
        val userIds = existingClaimEntities.map(CollectedClaimEntity::userId).distinct()
        return userIds.map { userId ->
            ConflictingCollectedUserClaims(
                userId = userId,
                claims = existingClaimEntities.filter { it.userId == userId }
                    .filter { entityValues.contains(it.value) }
                    .mapNotNull { claimManager.findById(it.claim) }
            )
        }
    }

    /**
     * Return the user info we have collected for the user identified by [userId].
     * Only return the user info that can be read accorded to the [context].
     */
    suspend fun findReadableUserInfoByUserId(
        context: Context,
        userId: UUID
    ): List<CollectedClaim> {
        return collectedClaimRepository.findByUserId(userId)
            .mapNotNull(collectedClaimMapper::toCollectedClaim)
            .filter { context.canRead(it.claim) }
    }

    /**
     * Update the claims collected for the [user] and return all the claims readable according to the [context].
     */
    @Transactional
    open suspend fun updateUserInfo(
        context: Context,
        user: User,
        updates: List<CollectedClaimUpdate>
    ): List<CollectedClaim> = coroutineScope {
        val applicableUpdates = updates.filter { context.canWrite(it.claim) }
        val existingEntities = collectedClaimRepository.findByUserId(user.id)
            .associateBy(CollectedClaimEntity::claim)
            .toMutableMap()

        val entitiesToDelete = applicableUpdates
            .filter { it.value == null }
            .mapNotNull { existingEntities.remove(it.claim.id) }
        val deferredDelete = async {
            collectedClaimRepository.deleteAll(entitiesToDelete)
        }

        val entitiesToUpdate = applicableUpdates
            .filter { it.value != null }
            .mapNotNull { update ->
                val entity = existingEntities[update.claim.id]
                entity?.let { update to entity }
            }
            .map { (update, entity) ->
                collectedClaimUpdateMapper.updateEntity(entity, update).also {
                    existingEntities[update.claim.id] = it
                }
            }

        val entitiesToCreate = applicableUpdates
            .filter { it.value != null }
            .map {
                collectedClaimUpdateMapper.toEntity(user.id, it)
            }
        val deferredSave = async {
            collectedClaimRepository.saveAll(entitiesToCreate + entitiesToUpdate)
                .collect()
        }

        awaitAll(deferredSave, deferredDelete)

        (existingEntities.values + entitiesToCreate)
            .mapNotNull { collectedClaimMapper.toCollectedClaim(it) }
            .filter { context.canRead(it.claim) }
    }

    /**
     * Return if the [value] is valid otherwise throws an exception.
     */
    fun validateAndConvertToUpdate(claim: Claim, value: Any?): CollectedClaimUpdate {
        if (value != null && claim.dataType.typeClass != value::class) {
            throw localizedExceptionOf(
                "claim.validate.invalid_type",
                "claim" to claim, "type" to claim.dataType
            )
        }
        return when(value) {
            null -> CollectedClaimUpdate(claim, Optional.empty())
            is String -> validateStringAndConvertToUpdate(claim, value)
            else -> throw localizedExceptionOf("claim.validate.unsupported_type", "claim" to claim)
        }
    }

    internal fun validateStringAndConvertToUpdate(claim: Claim, value: String): CollectedClaimUpdate {
        // FIXME add validation for email & phone number
        return CollectedClaimUpdate(claim, Optional.ofNullable(value.nullIfBlank()))
    }
}

data class ConflictingCollectedUserClaims(
    val userId: UUID,
    val claims: List<Claim>
)
