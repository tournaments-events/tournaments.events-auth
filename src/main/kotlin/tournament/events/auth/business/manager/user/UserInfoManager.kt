package tournament.events.auth.business.manager.user

import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import tournament.events.auth.business.manager.provider.ProviderUserInfoManager
import tournament.events.auth.business.model.user.RawUserInfo
import tournament.events.auth.business.security.Context
import java.util.*

@Singleton
class UserInfoManager(
    @Inject private val collectedUserInfoManager: CollectedUserInfoManager,
    @Inject private val providerUserInfoManager: ProviderUserInfoManager
) {

    /**
     * Merge the user info collected by:
     * - this application as a first-party.
     * - third-party providers used by the end-user to authenticate.
     */
    suspend fun aggregateUserInfo(
        context: Context,
        userId: UUID
    ): RawUserInfo = coroutineScope {
        val deferredCollectedUserInfoList = async {
            collectedUserInfoManager.findReadableUserInfoByUserId(context, userId) // FIXME: use user context
        }
        val deferredProviderUserInfoList = async {
            providerUserInfoManager.findByUserId(userId)
        }

        val merger = UserInfoMerger(
            userId = userId,
            collectedUserInfoList = deferredCollectedUserInfoList.await(),
            providerUserInfoList = deferredProviderUserInfoList.await()
        )
        merger.merge()
    }
}
