package tournament.events.auth.business.manager.user

import io.micronaut.transaction.annotation.Transactional
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import tournament.events.auth.business.mapper.CollectedUserInfoMapper
import tournament.events.auth.business.mapper.CollectedUserInfoUpdateMapper
import tournament.events.auth.business.model.user.CollectedUserInfo
import tournament.events.auth.business.model.user.CollectedUserInfoUpdate
import tournament.events.auth.business.model.user.User
import tournament.events.auth.business.security.Context
import tournament.events.auth.data.model.CollectedUserInfoEntity
import tournament.events.auth.data.repository.CollectedUserInfoRepository
import java.util.*

@Singleton
open class CollectedUserInfoManager(
    @Inject private val userInfoRepository: CollectedUserInfoRepository,
    @Inject private val userInfoMapper: CollectedUserInfoMapper,
    @Inject private val userInfoUpdateMapper: CollectedUserInfoUpdateMapper
) {

    /**
     * Return the user info we have collected for the user identified by [userId].
     * Only return the user info that can be read accorded to the [context].
     */
    suspend fun findReadableUserInfoByUserId(
        context: Context,
        userId: UUID
    ): List<CollectedUserInfo> {
        return userInfoRepository.findByUserId(userId)
            .mapNotNull(userInfoMapper::toCollectedUserInfo)
            .filter { context.canRead(it.claim) }
    }

    /**
     * Update the claims collected for the [user] and return all the claims readable according to the [context].
     */
    @Transactional
    open suspend fun updateUserInfo(
        context: Context,
        user: User,
        updates: List<CollectedUserInfoUpdate>
    ): List<CollectedUserInfo> = coroutineScope {
        val applicableUpdates = updates.filter { context.canWrite(it.claim) }
        val existingEntities = userInfoRepository.findByUserId(user.id)
            .associateBy(CollectedUserInfoEntity::claim)
            .toMutableMap()

        val entitiesToDelete = applicableUpdates
            .filter { it.value == null }
            .mapNotNull { existingEntities.remove(it.claim.id) }
        val deferredDelete = async {
            userInfoRepository.deleteAll(entitiesToDelete)
        }

        val entitiesToUpdate = applicableUpdates
            .filter { it.value != null }
            .mapNotNull { update ->
                val entity = existingEntities[update.claim.id]
                entity?.let { update to entity }
            }
            .map { (update, entity) ->
                userInfoUpdateMapper.updateEntity(entity, update).also {
                    existingEntities[update.claim.id] = it
                }
            }

        val entitiesToCreate = applicableUpdates
            .filter { it.value != null }
            .map {
                userInfoUpdateMapper.toEntity(user.id, it)
            }
        val deferredSave = async {
            userInfoRepository.saveAll(entitiesToCreate + entitiesToUpdate)
                .collect()
        }

        awaitAll(deferredSave, deferredDelete)

        (existingEntities.values + entitiesToCreate)
            .mapNotNull { userInfoMapper.toCollectedUserInfo(it) }
            .filter { context.canRead(it.claim) }
    }
}
