package tournament.events.auth.business.manager.password

import io.micronaut.http.HttpStatus
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.business.manager.user.ClaimManager
import tournament.events.auth.business.mapper.ClaimValueMapper
import tournament.events.auth.business.mapper.UserMapper
import tournament.events.auth.business.model.user.User
import tournament.events.auth.business.model.user.UserPasswordStatus
import tournament.events.auth.business.model.user.claim.Claim
import tournament.events.auth.config.model.EnabledPasswordAuthConfig
import tournament.events.auth.config.model.PasswordAuthConfig
import tournament.events.auth.config.model.orThrow
import tournament.events.auth.data.repository.CollectedUserInfoRepository
import tournament.events.auth.data.repository.UserRepository
import tournament.events.auth.data.repository.findByLogin
import tournament.events.auth.exception.httpExceptionOf

/**
 * Manager in charge of the authentication and registration of end-user using a password.
 */
@Singleton
class PasswordFlowManager(
    @Inject private val claimManager: ClaimManager,
    @Inject private val collectedUserInfoRepository: CollectedUserInfoRepository,
    @Inject private val userRepository: UserRepository,
    @Inject private val claimValueMapper: ClaimValueMapper,
    @Inject private val userMapper: UserMapper,
    @Inject private val uncheckedPasswordAuthConfig: PasswordAuthConfig
) {

    /**
     * True if the sign-in by login/password is enabled. False otherwise.
     */
    val signInEnabled: Boolean
        get() = uncheckedPasswordAuthConfig.orThrow().enabled

    /**
     * True if the sign-up by login/password is enabled. False otherwise.
     */
    val signUpEnabled: Boolean
        get() = uncheckedPasswordAuthConfig.orThrow().enabled

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
     * Sign-in the end-user using a [login] and its [password].
     */
    suspend fun signInWithPassword(
        login: String?,
        password: String?
    ): User {
        if (!signInEnabled) {
            throw httpExceptionOf(HttpStatus.BAD_REQUEST, "user.sign_in.disabled")
        }
        if (login.isNullOrBlank() || password.isNullOrBlank()) {
            throw httpExceptionOf(HttpStatus.BAD_REQUEST, "user.sign_in.invalid")
        }

        val user = findByLogin(login)
        val passwordStatus = user?.passwordStatus
        // The user does not exist or has been created using a third-party provider.
        if (user == null || passwordStatus == null) {
            throw httpExceptionOf(HttpStatus.BAD_REQUEST, "user.sign_in.invalid")
        }

        // FIXME: Handle the case where the password is still in creation.
        if (user.passwordStatus == UserPasswordStatus.COMPLETE && user.password != password) {
            throw httpExceptionOf(HttpStatus.BAD_REQUEST, "user.sign_in.invalid")
        }

        return user
    }

    /**
     * Return a list of [Claim] the end-user can use as a login for the password flow.
     */
    fun getSignInClaims(): List<Claim> {
        return uncheckedPasswordAuthConfig.orThrow()
            .loginClaims
            .mapNotNull { claimManager.findById(it.id) }
    }

    /**
     * Return a list of [Claim] that may be collected alongside the end-user password during the sign-up.
     */
    fun getSignUpClaims(): List<Claim> {
        return getSignInClaims()
    }
}
