package tournament.events.auth.business.manager

import io.micronaut.http.HttpStatus.BAD_REQUEST
import io.micronaut.http.HttpStatus.INTERNAL_SERVER_ERROR
import io.micronaut.transaction.annotation.Transactional
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.business.manager.provider.ProviderUserInfoManager
import tournament.events.auth.business.mapper.UserMapper
import tournament.events.auth.business.model.provider.EnabledProvider
import tournament.events.auth.business.model.user.RawUserInfo
import tournament.events.auth.business.model.user.User
import tournament.events.auth.business.model.user.UserMergingStrategy.BY_MAIL
import tournament.events.auth.business.model.user.UserMergingStrategy.NONE
import tournament.events.auth.business.model.user.UserStatus
import tournament.events.auth.config.model.AdvancedConfig
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
    @Inject private val providerUserInfoManager: ProviderUserInfoManager,
    @Inject private val collectedUserInfoRepository: CollectedUserInfoRepository,
    @Inject private val userRepository: UserRepository,
    @Inject private val advancedConfig: AdvancedConfig,
    @Inject private val userMapper: UserMapper,
    @Inject private val uncheckedPasswordAuthConfig: PasswordAuthConfig
) {

    /**
     * Find the end-user with a claim matching the [login].
     * The claims used to match the login are configured in [EnabledPasswordAuthConfig.loginClaims].
     */
    internal suspend fun findByLogin(login: String): User? {
        val loginClaims = uncheckedPasswordAuthConfig.orThrow().loginClaims
        val userInfo = collectedUserInfoRepository.findByLogin(login, loginClaims)
        return userInfo?.userId
            ?.let { userRepository.findById(it) }
            ?.let(userMapper::toUser)
    }

    /**
     * Find the end-user with a collected email claim matching the [email].
     */
    internal suspend fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
            ?.let(userMapper::toUser)
    }

    /**
     * Sign-in the end-user using a [login] and its [password].
     */
    suspend fun signInWithPassword(
        login: String,
        password: String
    ): User {
        val user = findByLogin(login) ?: throw httpExceptionOf(BAD_REQUEST, "user.sign_in.invalid")
        // FIXME: Check password
        return user
    }

    /**
     * Create a new [User]
     *
     * Depending on the configuration, the creation of the end-user may be complete with the
     * information in the [rawUserInfo] or may require more steps to complete (ex. more user info, email verification).
     */
    @Transactional
    internal open suspend fun createUser(
        status: UserStatus,
        collectedUserInfo: RawUserInfo?,
    ): User {
        val entity = UserEntity(
            status = status.name,
            creationDate = now()
        )
        userRepository.save(entity)

        return userMapper.toUser(entity)
    }

    /**
     * Create a new [User] or associate to an existing [User].
     * Then update the provider user info with the newly collected [providerUserInfo].
     *
     * Depending on the ```advanced.user-merging-strategy```, we may instead associate the [providerUserInfo] to
     * an existing user.
     */
    @Transactional
    open suspend fun createOrAssociateUserWithProviderUserInfo(
        provider: EnabledProvider,
        providerUserInfo: RawUserInfo
    ): CreateOrAssociateResult {
        return when (advancedConfig.orThrow().userMergingStrategy) {
            BY_MAIL -> createOrAssociateUserByEmailWithProviderUserInfo(provider, providerUserInfo)
            NONE -> createUserWithProviderUserInfo(provider, providerUserInfo)
        }
    }

    /**
     * Create a new [User] or associate it to a [User] that have the same email.
     * and update the user info collected by the [provider] with the [providerUserInfo].
     *
     * If the ```advanced.user-merging-strategy``` is set to ```by-mail```, we will check if we have an existing user
     * with the email first. If yes, we will only update the user info, otherwise, we will create it.
     *
     * The email is collected and copied as a first party data. We want this information to be stable
     * and not be affected by changes from the third party in the future.
     * Otherwise, an update from a provider may break our uniqueness and cause uncontrolled side effects.
     */
    @Transactional
    internal open suspend fun createOrAssociateUserByEmailWithProviderUserInfo(
        provider: EnabledProvider,
        providerUserInfo: RawUserInfo
    ): CreateOrAssociateResult {
        val email = providerUserInfo.email ?: throw httpExceptionOf(
            INTERNAL_SERVER_ERROR, "user.create_with_provider.missing_email",
            "providerId" to provider.id
        )
        val existingUser = findByEmail(email)
        val user = if (existingUser == null) {
            val collectedUserInfo = RawUserInfo(
                subject = "",
                email = email
            )
            createUser(
                status = UserStatus.COMPLETE,
                collectedUserInfo
            )
        } else existingUser

        providerUserInfoManager.saveUserInfo(
            provider = provider,
            userId = user.id,
            rawUserInfo = providerUserInfo
        )
        return CreateOrAssociateResult(
            created = existingUser == null,
            user = user
        )
    }

    @Transactional
    internal open suspend fun createUserWithProviderUserInfo(
        provider: EnabledProvider,
        providerUserInfo: RawUserInfo
    ): CreateOrAssociateResult {
        TODO("FIXME")
    }
}

data class CreateOrAssociateResult(
    val created: Boolean,
    val user: User
)
