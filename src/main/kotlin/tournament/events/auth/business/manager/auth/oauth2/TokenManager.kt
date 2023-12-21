package tournament.events.auth.business.manager.auth.oauth2

import jakarta.inject.Singleton
import tournament.events.auth.business.model.auth.oauth2.AuthorizeAttempt
import tournament.events.auth.business.model.auth.oauth2.AuthenticationToken

@Singleton
class TokenManager(

) {

    suspend fun generateTokens(
        authorizeAttempt: AuthorizeAttempt
    ): AuthenticationToken {
        TODO()
    }
}
