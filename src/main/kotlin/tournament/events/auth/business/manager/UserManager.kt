package tournament.events.auth.business.manager

import io.micronaut.http.HttpStatus.BAD_REQUEST
import jakarta.inject.Singleton
import tournament.events.auth.business.exception.BusinessException
import tournament.events.auth.business.model.provider.ProviderUserInfo
import tournament.events.auth.business.model.user.User
import tournament.events.auth.data.model.UserEntity
import tournament.events.auth.data.repository.UserRepository

@Singleton
class UserManager(
    private val userRepository: UserRepository
) {

    /**
     * Find the [User] associated to the [userId]
     */
    suspend fun findByProviderUserId(
        providerId: String,
        userId: String
    ): User? {
        TODO()
    }

    suspend fun createUser(
        email: String,
        username: String,
        password: String
    ): UserEntity {
        val user = userRepository.findByEmail(email)
        return if (user == null) {
            val newUser = UserEntity(
                username = username,
                email = email,
                password = password
            )
            userRepository.save(newUser)
        } else {
            throw BusinessException(BAD_REQUEST, "exception.user.email_already_used")
        }
    }

    suspend fun createOrAssociateUserWithUserDetails(
        userDetails: ProviderUserInfo
    ): CreateOrAssociateResult {
        return TODO()
    }

    suspend fun refreshUserDetails(
        user: User,
        userDetails: ProviderUserInfo
    ): User {
        TODO()
    }

    fun isMissingMandatoryUserDetails(user: User): Boolean {
        return TODO()
    }
}

data class CreateOrAssociateResult(
    val created: Boolean,
    val user: User
)
