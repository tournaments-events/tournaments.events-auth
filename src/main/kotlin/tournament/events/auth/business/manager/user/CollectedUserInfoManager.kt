package tournament.events.auth.business.manager.user

import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.business.mapper.CollectedUserInfoMapper
import tournament.events.auth.business.model.user.CollectedUserInfo
import tournament.events.auth.business.model.user.RawUserInfoUpdate
import tournament.events.auth.data.model.CollectedUserInfoEntity
import tournament.events.auth.data.repository.CollectedUserInfoRepository
import java.time.LocalDateTime.now
import java.util.*

@Singleton
class CollectedUserInfoManager(
    @Inject private val userInfoRepository: CollectedUserInfoRepository,
    @Inject private val userInfoMapper: CollectedUserInfoMapper
) {

    suspend fun findByUserId(userId: UUID): CollectedUserInfo? {
        return userInfoRepository.findById(userId)
            ?.let(userInfoMapper::toCollectedUserInfo)
    }

    suspend fun updateOrCreateUserInfo(
        userId: UUID,
        collectedUserInfo: RawUserInfoUpdate
    ): CollectedUserInfo {
        val now = now()
        val entity = userInfoRepository.findById(userId)
            ?: CollectedUserInfoEntity(
                userId = userId,
                collectedBits = ByteArray(0),
                creationDate = now(),
                updateDate = now()
            )
        updateUserInfo(entity, collectedUserInfo)
        userInfoRepository.save(entity)
        return userInfoMapper.toCollectedUserInfo(entity)
    }

    internal fun updateUserInfo(
        entity: CollectedUserInfoEntity,
        update: RawUserInfoUpdate
    ) {
        var updateCount = 0
        update.name?.let {
            TODO()
        }
        if (updateCount > 0) {
            entity.updateDate = now()
        }
    }
}
