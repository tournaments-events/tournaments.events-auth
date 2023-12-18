package tournament.events.auth.business.manager.auth

import jakarta.inject.Singleton
import tournament.events.auth.business.model.auth.AuthorizeAttempt
import tournament.events.auth.business.model.auth.Token
import tournament.events.auth.business.model.user.User

@Singleton
class TokenManager {

    suspend fun generateTokens(
        user: User,
        authorizeAttempt: AuthorizeAttempt
    ): Token {
        TODO()
    }
}
