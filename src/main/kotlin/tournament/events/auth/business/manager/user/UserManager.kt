package tournament.events.auth.business.manager.user

import io.micronaut.transaction.annotation.Transactional
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.business.mapper.ClaimValueMapper
import tournament.events.auth.business.mapper.UserMapper
import tournament.events.auth.business.model.user.User
import tournament.events.auth.business.model.user.UserStatus
import tournament.events.auth.business.model.user.claim.OpenIdClaim.EMAIL
import tournament.events.auth.config.model.PasswordAuthConfig
import tournament.events.auth.data.model.UserEntity
import tournament.events.auth.data.repository.CollectedUserInfoRepository
import tournament.events.auth.data.repository.UserRepository
import tournament.events.auth.data.repository.findByLogin
import java.time.LocalDateTime.now

@Singleton
open class UserManager(
    @Inject private val collectedUserInfoRepository: CollectedUserInfoRepository,
    @Inject private val userRepository: UserRepository,
    @Inject private val claimValueMapper: ClaimValueMapper,
    @Inject private val userMapper: UserMapper,
    @Inject private val uncheckedPasswordAuthConfig: PasswordAuthConfig
) {

    /**
     * Find the end-user with a collected email claim matching the [email].
     */
    internal suspend fun findByEmail(email: String): User? {
        val userInfo = collectedUserInfoRepository.findByLogin(email, listOf(EMAIL))
        return userInfo?.userId
            ?.let { userRepository.findById(it) }
            ?.let(userMapper::toUser)
    }

    /**
     * Create a new [User].
     */
    @Transactional
    internal open suspend fun createUser(): User {
        val entity = UserEntity(
            status = UserStatus.COMPLETE.name,
            creationDate = now()
        )
        userRepository.save(entity)

        return userMapper.toUser(entity)
    }
}

data class CreateOrAssociateResult(
    val created: Boolean,
    val user: User
)
