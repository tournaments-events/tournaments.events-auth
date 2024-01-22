package com.sympauthy.business.manager.user

import com.sympauthy.business.mapper.ClaimValueMapper
import com.sympauthy.business.mapper.UserMapper
import com.sympauthy.business.model.user.User
import com.sympauthy.business.model.user.UserStatus
import com.sympauthy.business.model.user.claim.OpenIdClaim.EMAIL
import com.sympauthy.config.model.PasswordAuthConfig
import com.sympauthy.data.model.UserEntity
import com.sympauthy.data.repository.CollectedUserInfoRepository
import com.sympauthy.data.repository.UserRepository
import com.sympauthy.data.repository.findByLogin
import io.micronaut.transaction.annotation.Transactional
import jakarta.inject.Inject
import jakarta.inject.Singleton
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
