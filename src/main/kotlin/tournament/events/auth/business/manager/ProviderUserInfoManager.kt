package tournament.events.auth.business.manager

import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.business.model.provider.EnabledProvider
import tournament.events.auth.business.model.provider.ProviderCredentials
import tournament.events.auth.business.model.provider.ProviderUserInfo
import tournament.events.auth.business.model.provider.config.ProviderUserInfoConfig
import tournament.events.auth.client.UserInfoEndpointClient

@Singleton
class ProviderUserInfoManager(
    @Inject private val userInfoEndpointClient: UserInfoEndpointClient
) {

    suspend fun fetchUserInfo(
        provider: EnabledProvider,
        credentials: ProviderCredentials
    ): ProviderUserInfo {
        val response = userInfoEndpointClient.fetchUserInfo(
            userInfoConfig = getUserInfoConfig(provider),
            credentials = credentials
        )
    }

    fun getUserInfoConfig(provider: EnabledProvider): ProviderUserInfoConfig {
        return when {
            else -> TODO() // throw
        }
    }
}
