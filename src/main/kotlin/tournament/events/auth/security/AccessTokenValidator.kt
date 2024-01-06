package tournament.events.auth.security

import io.micronaut.http.HttpStatus.UNAUTHORIZED
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.token.validator.TokenValidator
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.reactive.publish
import org.reactivestreams.Publisher
import tournament.events.auth.api.exception.OAuth2Exception
import tournament.events.auth.api.exception.toHttpException
import tournament.events.auth.business.manager.auth.oauth2.TokenManager
import tournament.events.auth.business.manager.jwt.JwtManager
import tournament.events.auth.business.manager.jwt.JwtManager.Companion.PUBLIC_KEY
import tournament.events.auth.exception.LocalizedException
import tournament.events.auth.exception.httpExceptionOf
import tournament.events.auth.exception.toHttpException
import java.util.*

/**
 * [TokenValidator] that validates token we have issued with this authentication server.
 *
 * To authorize the user, we need:
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
            throw httpExceptionOf(UNAUTHORIZED, "access.invalid_token")
        }

        val authenticationToken = try {
            tokenManager.getAuthenticationToken(decodedToken)
        } catch (e: OAuth2Exception) {
            throw e.toHttpException(UNAUTHORIZED)
        }
        val authentication = UserAuthentication(
            authenticationToken
        )
        send(authentication)
    }
}
