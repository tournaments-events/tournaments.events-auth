package tournament.events.auth.business.manager

import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.business.manager.provider.ProviderUserInfoManager
import tournament.events.auth.business.mapper.UserMapper
import tournament.events.auth.business.model.provider.EnabledProvider
import tournament.events.auth.business.model.user.RawUserInfo
import tournament.events.auth.business.model.user.User
import tournament.events.auth.business.model.user.UserMergingStrategy.BY_MAIL
import tournament.events.auth.business.model.user.UserMergingStrategy.NONE
import tournament.events.auth.config.model.AdvancedConfig
import tournament.events.auth.config.model.orThrow
import tournament.events.auth.data.model.UserEntity
import tournament.events.auth.data.repository.UserRepository
import java.time.LocalDateTime

@Singleton
class UserManager(
    @Inject private val providerUserInfoManager: ProviderUserInfoManager,
    @Inject private val userRepository: UserRepository,
    @Inject private val advancedConfig: AdvancedConfig,
    @Inject private val userMapper: UserMapper
) {

    suspend fun createOrAssociateUserWithUserInfo(
        provider: EnabledProvider,
        rawUserInfo: RawUserInfo
    ): CreateOrAssociateResult {
        val existingUserEntity = when (advancedConfig.orThrow().userMergingStrategy) {
            BY_MAIL -> rawUserInfo.email?.let { userRepository.findByEmail(it) }
            NONE -> null
        }
        val userEntity = if (existingUserEntity == null) {
            val newUser = UserEntity(
                email = rawUserInfo.email,
                creationDate = LocalDateTime.now()
            )
            userRepository.save(newUser)
        } else existingUserEntity

        providerUserInfoManager.saveUserInfo(
            provider = provider,
            userId = userEntity.id!!,
            rawUserInfo = rawUserInfo
        )
        return CreateOrAssociateResult(
            created = existingUserEntity == null,
            user = userMapper.toUser(userEntity)
        )
    }
}

data class CreateOrAssociateResult(
    val created: Boolean,
    val user: User
)
