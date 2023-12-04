package tournament.events.auth.business.manager

import io.micronaut.http.HttpStatus.BAD_REQUEST
import jakarta.inject.Singleton
import tournament.events.auth.business.exception.BusinessException
import tournament.events.auth.data.model.UserEntity
import tournament.events.auth.data.repository.UserRepository

@Singleton
class UserManager(
    private val userRepository: UserRepository
) {

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
            userRepository.insert(newUser)
        } else {
            throw BusinessException(BAD_REQUEST, "exception.user.email_already_used")
        }
    }
}
