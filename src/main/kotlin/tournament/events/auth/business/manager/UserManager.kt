package tournament.events.auth.business.manager

import io.micronaut.http.HttpStatus.BAD_REQUEST
import io.reactivex.rxjava3.core.Single
import jakarta.inject.Singleton
import tournament.events.auth.business.exception.maybeBusinessExceptionOf
import tournament.events.auth.data.model.UserEntity
import tournament.events.auth.data.repository.UserRepository

@Singleton
class UserManager(
    private val userRepository: UserRepository
) {

    fun createUser(
        email: String,
        username: String,
        password: String
    ): Single<UserEntity> {
        return userRepository.findByEmail(email)
            .flatMap {
                maybeBusinessExceptionOf<UserEntity>(
                    BAD_REQUEST,
                    "exception.user.email_already_used"
                )
            }
            .switchIfEmpty(
                doCreateUser(
                    email = email,
                    username = username,
                    password = password
                )
            )
    }

    internal fun doCreateUser(
        email: String,
        username: String,
        password: String
    ): Single<UserEntity> {
        val user = UserEntity(
            username = username,
            email = email,
            password = password
        )
        return userRepository.insert(user)
    }
}
