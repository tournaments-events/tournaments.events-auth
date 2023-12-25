package tournament.events.auth.business.manager.user

import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.business.mapper.CollectedUserInfoMapper
import tournament.events.auth.business.model.user.CollectedUserInfo
import tournament.events.auth.data.repository.CollectedUserInfoRepository
import java.util.UUID

@Singleton
class CollectedUserInfoManager(
    @Inject private val collectedUserInfoRepository: CollectedUserInfoRepository,
    @Inject private val collectedUserInfoMapper: CollectedUserInfoMapper
) {

    suspend fun findByUserId(userId: UUID): CollectedUserInfo? {
        return collectedUserInfoRepository.findById(userId)
            ?.let(collectedUserInfoMapper::toCollectedUserInfo)
    }
}
