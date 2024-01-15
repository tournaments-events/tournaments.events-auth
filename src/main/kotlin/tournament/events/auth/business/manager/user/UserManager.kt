package tournament.events.auth.business.manager.user

import io.micronaut.http.HttpStatus.BAD_REQUEST
import io.micronaut.transaction.annotation.Transactional
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.business.mapper.ClaimValueMapper
import tournament.events.auth.business.mapper.UserMapper
import tournament.events.auth.business.model.user.User
import tournament.events.auth.business.model.user.UserPasswordStatus.COMPLETE
import tournament.events.auth.business.model.user.UserStatus
import tournament.events.auth.business.model.user.claim.OpenIdClaim.EMAIL
import tournament.events.auth.config.model.EnabledPasswordAuthConfig
import tournament.events.auth.config.model.PasswordAuthConfig
import tournament.events.auth.config.model.orThrow
import tournament.events.auth.data.model.UserEntity
import tournament.events.auth.data.repository.CollectedUserInfoRepository
import tournament.events.auth.data.repository.UserRepository
import tournament.events.auth.data.repository.findByLogin
import tournament.events.auth.exception.httpExceptionOf
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
     * Find the end-user with a claim matching the [login].
     * The claims used to match the login are configured in [EnabledPasswordAuthConfig.loginClaims].
     */
    internal suspend fun findByLogin(login: String): User? {
        val loginClaims = uncheckedPasswordAuthConfig.orThrow().loginClaims
        val loginValue = claimValueMapper.toEntity(login) ?: return null
        val userInfo = collectedUserInfoRepository.findByLogin(loginValue, loginClaims)
        return userInfo?.userId
            ?.let { userRepository.findById(it) }
            ?.let(userMapper::toUser)
    }

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
     * Sign-in the end-user using a [login] and its [password].
     */
    suspend fun signInWithPassword(
        login: String?,
        password: String?
    ): User {
        if (login.isNullOrBlank() || password.isNullOrBlank()) {
            throw httpExceptionOf(BAD_REQUEST, "user.sign_in.invalid")
        }

        val user = findByLogin(login)
        val passwordStatus = user?.passwordStatus
        // The user does not exist or has been created using a third-party provider.
        if (user == null || passwordStatus == null) {
            throw httpExceptionOf(BAD_REQUEST, "user.sign_in.invalid")
        }

        // FIXME: Handle the case where the password is still in creation.
        if (user.passwordStatus == COMPLETE && user.password != password) {
            throw httpExceptionOf(BAD_REQUEST, "user.sign_in.invalid")
        }

        return user
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
