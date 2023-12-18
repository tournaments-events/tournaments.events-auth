package tournament.events.auth.business.manager.provider

import com.jayway.jsonpath.JsonPath
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.business.mapper.ProviderUserInfoMapper
import tournament.events.auth.business.model.provider.EnabledProvider
import tournament.events.auth.business.model.provider.ProviderCredentials
import tournament.events.auth.business.model.provider.ProviderUserInfo
import tournament.events.auth.business.model.provider.RawProviderUserInfo
import tournament.events.auth.client.UserInfoEndpointClient
import tournament.events.auth.data.repository.ProviderUserInfoRepository
import java.time.LocalDateTime
import java.util.UUID

@Singleton
class ProviderUserInfoManager(
    @Inject private val userInfoRepository: ProviderUserInfoRepository,
    @Inject private val userInfoEndpointClient: UserInfoEndpointClient,
    @Inject private val userInfoMapper: ProviderUserInfoMapper
) {

    suspend fun findByProviderAndSubject(
        provider: EnabledProvider,
        subject: String
    ): ProviderUserInfo? {
        return userInfoRepository.findByProviderIdAndSubject(
            providerId = provider.id,
            subject = subject
        )?.let(userInfoMapper::toProviderUserInfo)
    }

    suspend fun fetchUserInfo(
        provider: EnabledProvider,
        credentials: ProviderCredentials
    ): RawProviderUserInfo {
        val rawUserInfoMap = userInfoEndpointClient.fetchUserInfo(
            userInfoConfig = provider.userInfo,
            credentials = credentials
        )

        val document = JsonPath.parse(rawUserInfoMap)
        return provider.userInfo.paths.entries
            .fold(RawProviderUserInfoBuilder()) { builder, (pathKey, path) ->
                builder.withUserInfo(document, pathKey, path)
            }
            .build(provider)
    }

    suspend fun saveUserInfo(
        provider: EnabledProvider,
        userId: UUID,
        rawUserInfo: RawProviderUserInfo
    ): ProviderUserInfo {
        val now = LocalDateTime.now()
        val entity = userInfoMapper.toEntity(
            providerId = provider.id,
            userId = userId,
            userInfo = rawUserInfo,
            lastFetchDate = now,
            lastChangeDate = now
        )
        userInfoRepository.save(entity)
        return userInfoMapper.toProviderUserInfo(entity)
    }

    fun refreshUserInfo(
        existingUserInfo: ProviderUserInfo,
        newUserInfo: RawProviderUserInfo
    ) {
        if (existingUserInfo.userInfo == newUserInfo) {
            return
        }
        TODO()
    }
}
