package tournament.events.auth.business.manager.auth.oauth2

import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import tournament.events.auth.api.exception.oauth2ExceptionOf
import tournament.events.auth.business.manager.jwt.JwtManager
import tournament.events.auth.business.mapper.EncodedAuthenticationTokenMapper
import tournament.events.auth.business.model.auth.oauth2.AuthenticationTokenType.ACCESS
import tournament.events.auth.business.model.auth.oauth2.AuthenticationTokenType.REFRESH
import tournament.events.auth.business.model.auth.oauth2.AuthorizeAttempt
import tournament.events.auth.business.model.auth.oauth2.EncodedAuthenticationToken
import tournament.events.auth.business.model.auth.oauth2.OAuth2ErrorCode.INVALID_GRANT
import tournament.events.auth.business.model.auth.oauth2.OAuth2ErrorCode.SERVER_ERROR
import tournament.events.auth.config.model.AuthConfig
import tournament.events.auth.config.model.EnabledAuthConfig
import tournament.events.auth.config.model.orThrow
import tournament.events.auth.data.model.AuthenticationTokenEntity
import tournament.events.auth.data.repository.AuthenticationTokenRepository
import java.time.LocalDateTime.now
import java.time.ZoneOffset.UTC
import java.util.*

@Singleton
class TokenManager(
    @Inject private val jwtManager: JwtManager,
    @Inject private val tokenRepository: AuthenticationTokenRepository,
    @Inject private val encodedTokenMapper: EncodedAuthenticationTokenMapper,
    @Inject private val authConfig: AuthConfig
) {

    suspend fun generateTokens(
        authorizeAttempt: AuthorizeAttempt
    ): GenerateTokenResult = coroutineScope {
        if (authorizeAttempt.expired) {
            throw oauth2ExceptionOf(INVALID_GRANT, "token.expired", "description.oauth2.expired")
        }

        val userId = authorizeAttempt.userId ?: throw oauth2ExceptionOf(
            SERVER_ERROR, "token.attempt_missing_user"
        )
        val enabledAuthConfig = authConfig.orThrow()

        val tokens = mutableListOf(
            async {
                generateAccessToken(authorizeAttempt, userId, enabledAuthConfig)
            }
        )
        if (enabledAuthConfig.token.refreshEnabled) {
            tokens.add(
                async {
                    generateRefreshToken(authorizeAttempt, userId, enabledAuthConfig)
                }
            )
        }

        val (access, refresh) = awaitAll(*tokens.toTypedArray())
        GenerateTokenResult(
            accessToken = access,
            refreshToken = refresh
        )
    }

    internal suspend fun generateAccessToken(
        authorizeAttempt: AuthorizeAttempt,
        userId: UUID,
        authConfig: EnabledAuthConfig
    ): EncodedAuthenticationToken {
        val issueDate = now()
        val expirationDate = issueDate.plus(authConfig.token.accessExpiration)
        val entity = AuthenticationTokenEntity(
            userId = userId,
            type = ACCESS.name,
            clientId = authorizeAttempt.clientId,
            issueDate = issueDate,
            expirationDate = expirationDate
        ).let { tokenRepository.save(it) }

        val encodedToken = jwtManager.create("access") {
            entity.id?.toString()?.let(this::withJWTId)
            authConfig.audience?.let { this.withAudience(it) }
            withSubject(userId.toString())
            withIssuedAt(issueDate.toInstant(UTC))
            withExpiresAt(expirationDate.toInstant(UTC))
        }

        return encodedTokenMapper.toEncodedAuthenticationToken(entity, encodedToken)
    }

    internal suspend fun generateRefreshToken(
        authorizeAttempt: AuthorizeAttempt,
        userId: UUID,
        authConfig: EnabledAuthConfig
    ): EncodedAuthenticationToken {
        val issueDate = now()
        val expirationDate = authConfig.token.refreshExpiration?.let(issueDate::plus)
        val entity = AuthenticationTokenEntity(
            userId = userId,
            type = REFRESH.name,
            clientId = authorizeAttempt.clientId,
            issueDate = issueDate,
            expirationDate = expirationDate
        ).let { tokenRepository.save(it) }

        val encodedToken = jwtManager.create("refresh") {
            entity.id?.toString()?.let(this::withJWTId)
            authConfig.audience?.let { this.withAudience(it) }
            withSubject(userId.toString())
            withIssuedAt(issueDate.toInstant(UTC))
            expirationDate?.toInstant(UTC)?.let(this::withExpiresAt)
        }

        return encodedTokenMapper.toEncodedAuthenticationToken(entity, encodedToken)
    }
}

data class GenerateTokenResult(
    val accessToken: EncodedAuthenticationToken,
    val refreshToken: EncodedAuthenticationToken?
)
