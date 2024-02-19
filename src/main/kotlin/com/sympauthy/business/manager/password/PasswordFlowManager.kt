package com.sympauthy.business.manager.password

import com.sympauthy.business.exception.businessExceptionOf
import com.sympauthy.business.manager.ClaimManager
import com.sympauthy.business.manager.SignUpFlowManager
import com.sympauthy.business.manager.SignUpResult
import com.sympauthy.business.manager.user.CollectedClaimManager
import com.sympauthy.business.manager.user.UserManager
import com.sympauthy.business.mapper.ClaimValueMapper
import com.sympauthy.business.mapper.UserMapper
import com.sympauthy.business.model.user.CollectedClaimUpdate
import com.sympauthy.business.model.user.User
import com.sympauthy.business.model.user.UserStatus
import com.sympauthy.business.model.user.claim.Claim
import com.sympauthy.business.model.user.claim.OpenIdClaim
import com.sympauthy.business.security.AdminContext
import com.sympauthy.config.model.PasswordAuthConfig
import com.sympauthy.config.model.orThrow
import com.sympauthy.data.repository.CollectedClaimRepository
import com.sympauthy.data.repository.UserRepository
import com.sympauthy.data.repository.findAnyClaimMatching
import com.sympauthy.exception.httpExceptionOf
import io.micronaut.http.HttpStatus.BAD_REQUEST
import io.micronaut.transaction.annotation.Transactional
import jakarta.inject.Inject
import jakarta.inject.Singleton

/**
 * Manager in charge of the authentication and registration of end-user using a password.
 */
@Singleton
open class PasswordFlowManager(
    @Inject private val claimManager: ClaimManager,
    @Inject private val collectedClaimManager: CollectedClaimManager,
    @Inject private val collectedClaimRepository: CollectedClaimRepository,
    @Inject private val passwordManager: PasswordManager,
    @Inject private val signUpFlowManager: SignUpFlowManager,
    @Inject private val userManager: UserManager,
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
        val userInfo = collectedClaimRepository.findAnyClaimMatching(
            claimIds = loginClaims.map(OpenIdClaim::id),
            value = claimValueMapper.toEntity(login) ?: return null,
        )
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
            throw httpExceptionOf(BAD_REQUEST, "password.flow.sign_in.disabled")
        }
        if (login.isNullOrBlank() || password.isNullOrBlank()) {
            throw httpExceptionOf(BAD_REQUEST, "password.flow.sign_in.invalid")
        }

        val user = findByLogin(login)
        // The user does not exist or has been created using a third-party provider.
        if (user == null || user.status != UserStatus.ENABLED) {
            throw httpExceptionOf(BAD_REQUEST, "password.flow.sign_in.invalid")
        }

        if (!passwordManager.arePasswordMatching(user, password)) {

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

    /**
     * Create a new user with the provided claims [updates] and [password].
     */
    @Transactional
    open suspend fun signUpWithClaimsAndPassword(
        unfilteredUpdates: List<CollectedClaimUpdate>,
        password: String
    ): SignUpResult {
        val claimUpdateMap = getSignUpClaims().associateWith { claim ->
            unfilteredUpdates.firstOrNull { it.claim == claim }
        }
        val claimUpdates = claimUpdateMap.values.filterNotNull()

        checkForMissingClaims(claimUpdateMap)
        passwordManager.validatePassword(password)
        signUpFlowManager.checkForConflictingUsers(claimUpdates)

        val user = userManager.createUser()
        val collectedClaims = collectedClaimManager.updateUserInfo(
            context = AdminContext,
            user = user,
            updates = claimUpdates
        )
        passwordManager.createPassword(user, password)

        return signUpFlowManager.checkIfSignUpIsComplete(
            user = user,
            collectedClaims = collectedClaims
        )
    }

    internal fun checkForMissingClaims(claimUpdateMap: Map<Claim, CollectedClaimUpdate?>) {
        val missingClaim = claimUpdateMap.filterValues { it == null }
            .keys
            .firstOrNull()
        if (missingClaim != null) {
            throw businessExceptionOf(
                BAD_REQUEST, "password.flow.sign_up.missing_claim", "password.flow.sign_up.missing_claim",
                "claim" to missingClaim.id
            )
        }
    }
}
