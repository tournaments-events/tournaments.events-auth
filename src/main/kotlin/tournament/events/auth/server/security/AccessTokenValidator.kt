package tournament.events.auth.server.security

import com.auth0.jwt.interfaces.DecodedJWT
import io.micronaut.http.HttpStatus.UNAUTHORIZED
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.token.validator.TokenValidator
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.reactive.publish
import org.reactivestreams.Publisher
import tournament.events.auth.business.exception.businessExceptionOf
import tournament.events.auth.business.manager.auth.oauth2.TokenManager
import tournament.events.auth.business.manager.jwt.JwtManager
import tournament.events.auth.business.manager.jwt.JwtManager.Companion.PUBLIC_KEY
import tournament.events.auth.business.model.oauth2.AuthenticationToken
import tournament.events.auth.business.model.oauth2.AuthenticationTokenType.ACCESS
import tournament.events.auth.exception.LocalizedException
import tournament.events.auth.exception.httpExceptionOf
import tournament.events.auth.exception.toHttpException
import java.util.*

/**
 * As we are the authorization server, to authorize the user, we need:
 * - to decode the token.
 * - to validate the token signature against our [PUBLIC_KEY] signature key.
 * - to check the token is not expired.
 * - to validate the token is an access token.
 * - to retrieve the user and scope this token is associated to.
 * - to check the subject and the user id matches.
 */
@Singleton
class AccessTokenValidator<T>(
    @Inject private val tokenManager: TokenManager,
    @Inject private val jwtManager: JwtManager
) : TokenValidator<T> {

    override fun validateToken(token: String, request: T): Publisher<Authentication> = publish {
        val decodedToken = try {
            jwtManager.decodeAndVerify(PUBLIC_KEY, token)
        } catch (e: LocalizedException) {
            throw e.toHttpException(UNAUTHORIZED)
        }

        val userId = try {
            UUID.fromString(decodedToken.subject)
        } catch (e: IllegalArgumentException) {
            throw businessExceptionOf(UNAUTHORIZED, "access.invalid_token")
        }

        val authenticationToken = getAuthenticationToken(decodedToken)
        val authentication = UserAuthentication(
            authenticationToken
        )
        send(authentication)
    }

    private suspend fun getAuthenticationToken(decodedToken: DecodedJWT): AuthenticationToken {
        val id = try {
            UUID.fromString(decodedToken.id)
        } catch (e: IllegalArgumentException) {
            throw httpExceptionOf(UNAUTHORIZED, "access.invalid_token")
        }
        val token = tokenManager.findById(id)
        if (token?.type != ACCESS || decodedToken.subject != token.userId.toString()) {
            throw httpExceptionOf(UNAUTHORIZED, "access.invalid_token")
        }
        return token
    }
}
