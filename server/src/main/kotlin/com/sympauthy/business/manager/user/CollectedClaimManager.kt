package com.sympauthy.business.manager.user

import com.sympauthy.business.mapper.CollectedClaimMapper
import com.sympauthy.business.mapper.CollectedUserInfoUpdateMapper
import com.sympauthy.business.model.user.CollectedClaim
import com.sympauthy.business.model.user.CollectedClaimUpdate
import com.sympauthy.business.model.user.User
import com.sympauthy.business.model.user.claim.Claim
import com.sympauthy.data.model.CollectedClaimEntity
import com.sympauthy.data.repository.CollectedClaimRepository
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
    @Inject private val collectedClaimRepository: CollectedClaimRepository,
    @Inject private val collectedClaimMapper: CollectedClaimMapper,
    @Inject private val collectedClaimUpdateMapper: CollectedUserInfoUpdateMapper
) {

    /**
     * Return the user info we have collected for the user identified by [userId].
     * [scopes] can be provided to return only the claims accessible by the provided [scopes].
     */
    suspend fun findReadableUserInfoByUserId(
        userId: UUID,
        scopes: List<String>? = null
    ): List<CollectedClaim> {
        var claims = collectedClaimRepository.findByUserId(userId)
            .asSequence()
            .mapNotNull(collectedClaimMapper::toCollectedClaim)
        if (scopes != null) {
            claims = claims.filter { it.claim.canBeRead(scopes) }
        }
        return claims.toList()
    }

    /**
     * Update the claims collected for the [user] and return all the claims readable according to the [scopes].
     * Only claims that are editable according to the [scopes] will be modified. Other update will be ignored.
     * If [scopes] is null, it forces the application of all [updates] and return all claims.
     */
    @Transactional
    open suspend fun updateUserInfo(
        user: User,
        scopes: List<String>? = null,
        updates: List<CollectedClaimUpdate>
    ): List<CollectedClaim> = coroutineScope {
        val applicableUpdates = if (scopes != null) {
            updates.filter { it.claim.canBeWritten(scopes) }
        } else updates

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

        val collectedClaims = (existingEntities.values + entitiesToCreate)
            .mapNotNull { collectedClaimMapper.toCollectedClaim(it) }
        if (scopes != null) {
            collectedClaims.filter { it.claim.canBeRead(scopes) }
        } else {
            collectedClaims
        }
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
        return when (value) {
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
