package tournament.events.auth.business.manager.auth.oauth2

import com.auth0.jwt.interfaces.DecodedJWT
import io.micronaut.transaction.annotation.Transactional
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.*
import tournament.events.auth.api.exception.oauth2ExceptionOf
import tournament.events.auth.business.manager.jwt.JwtManager
import tournament.events.auth.business.manager.jwt.JwtManager.Companion.REFRESH_KEY
import tournament.events.auth.business.mapper.AuthenticationTokenMapper
import tournament.events.auth.business.model.client.Client
import tournament.events.auth.business.model.oauth2.AuthenticationToken
import tournament.events.auth.business.model.oauth2.AuthorizeAttempt
import tournament.events.auth.business.model.oauth2.EncodedAuthenticationToken
import tournament.events.auth.business.model.oauth2.OAuth2ErrorCode.INVALID_GRANT
import tournament.events.auth.business.model.oauth2.OAuth2ErrorCode.SERVER_ERROR
import tournament.events.auth.data.repository.AuthenticationTokenRepository
import tournament.events.auth.exception.LocalizedException
import java.util.*

@Singleton
open class TokenManager(
    @Inject private val jwtManager: JwtManager,
    @Inject private val accessTokenGenerator: AccessTokenGenerator,
    @Inject private val refreshTokenGenerator: RefreshTokenGenerator,
    @Inject private val tokenRepository: AuthenticationTokenRepository,
    @Inject private val tokenMapper: AuthenticationTokenMapper
) {

    /**
     * Return the [AuthenticationToken] identified by [id], null otherwise.
     */
    suspend fun findById(id: UUID): AuthenticationToken? {
        return tokenRepository.findById(id)?.let(tokenMapper::toToken)
    }

    /**
     * Revoke the token identified by [id].
     * A revoked token cannot be used anymore whether is for authentication or for refresh.
     */
    suspend fun revokeToken(id: UUID) {
        return tokenRepository.updateRevokedById(id, true)
    }

    @Transactional
    @OptIn(ExperimentalCoroutinesApi::class)
    open suspend fun generateTokens(
        authorizeAttempt: AuthorizeAttempt
    ): GenerateTokenResult = coroutineScope {
        if (authorizeAttempt.expired) {
            throw oauth2ExceptionOf(INVALID_GRANT, "token.expired", "description.oauth2.expired")
        }

        val userId = authorizeAttempt.userId ?: throw oauth2ExceptionOf(
            SERVER_ERROR, "token.attempt_missing_user"
        )

        val deferredAccessToken = async {
            accessTokenGenerator.generateAccessToken(authorizeAttempt, userId)
        }
        val deferredRefreshToken = async {
            refreshTokenGenerator.generateRefreshToken(authorizeAttempt, userId)
        }

        awaitAll(deferredAccessToken, deferredRefreshToken)
        GenerateTokenResult(
            accessToken = deferredAccessToken.getCompleted(),
            refreshToken = deferredRefreshToken.getCompleted()
        )
    }

    /**
     * Decodes and verify the [encodedRefreshToken] and issues a new access token.
     *
     * Additionally, a new refresh token may be issued if the refresh token expires
     * before the expiration of the new access token.
     *
     * Throws an [LocalizedException] if the refresh token validation fails:
     * - one of the validation of [JwtManager.decodeAndVerify].
     * - the [client] does not match the one we have issued the token too.
     */
    @Transactional
    open suspend fun refreshToken(
        client: Client,
        encodedRefreshToken: String
    ): List<EncodedAuthenticationToken> = supervisorScope {
        val decodedToken = try {
            jwtManager.decodeAndVerify(REFRESH_KEY, encodedRefreshToken)
        } catch (e: LocalizedException) {
            throw oauth2ExceptionOf(INVALID_GRANT, e.detailsId)
        }

        val refreshToken = getAuthenticationToken(decodedToken)
        if (refreshToken.clientId != client.id) {
            throw oauth2ExceptionOf(INVALID_GRANT, "token.mismatching_client")
        }

        val accessToken = accessTokenGenerator.generateAccessToken(refreshToken)
        val refreshedRefreshToken = if (shouldRefreshToken(refreshToken, accessToken)) {
            refreshRefreshToken(refreshToken)
        } else null

        listOfNotNull(accessToken, refreshedRefreshToken)
    }

    internal fun shouldRefreshToken(
        refreshToken: AuthenticationToken,
        accessToken: EncodedAuthenticationToken
    ): Boolean {
        return when {
            refreshToken.expirationDate == null -> false
            accessToken.expirationDate == null || refreshToken.expirationDate.isBefore(accessToken.expirationDate) -> true
            else -> false
        }
    }

    internal suspend fun refreshRefreshToken(
        refreshToken: AuthenticationToken
    ): EncodedAuthenticationToken {
        TODO()
    }

    /**
     * Return the information we stored about the [decodedToken] when we issued it.
     *
     * Throws an [OAuth2Exception] if:
     * - the identifier of the token cannot be decoded.
     * - the token cannot be found in the database despite being signed with our signature.
     * - the token has been revoked.
     */
    suspend fun getAuthenticationToken(decodedToken: DecodedJWT): AuthenticationToken {
        val id = try {
            UUID.fromString(decodedToken.id)
        } catch (e: IllegalArgumentException) {
            throw oauth2ExceptionOf(INVALID_GRANT, "token.invalid_token_id")
        }
        val token = findById(id)
        return when {
            token == null -> throw oauth2ExceptionOf(INVALID_GRANT, "token.invalid_token_id")
            decodedToken.subject != token.userId.toString() -> throw oauth2ExceptionOf(
                INVALID_GRANT, "token.invalid_token_id"
            )

            token.revoked -> throw oauth2ExceptionOf(INVALID_GRANT, "token.revoked")
            else -> token
        }
    }
}

data class GenerateTokenResult(
    val accessToken: EncodedAuthenticationToken,
    val refreshToken: EncodedAuthenticationToken?
)
