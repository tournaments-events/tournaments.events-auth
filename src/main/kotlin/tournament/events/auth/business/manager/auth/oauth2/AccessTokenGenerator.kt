package tournament.events.auth.business.manager.auth.oauth2

import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.business.manager.jwt.JwtManager
import tournament.events.auth.business.mapper.EncodedAuthenticationTokenMapper
import tournament.events.auth.business.model.oauth2.AuthenticationToken
import tournament.events.auth.business.model.oauth2.AuthenticationTokenType
import tournament.events.auth.business.model.oauth2.AuthorizeAttempt
import tournament.events.auth.business.model.oauth2.EncodedAuthenticationToken
import tournament.events.auth.config.model.AuthConfig
import tournament.events.auth.config.model.orThrow
import tournament.events.auth.data.model.AuthenticationTokenEntity
import tournament.events.auth.data.repository.AuthenticationTokenRepository
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@Singleton
class AccessTokenGenerator(
    @Inject private val jwtManager: JwtManager,
    @Inject private val tokenRepository: AuthenticationTokenRepository,
    @Inject private val tokenMapper: EncodedAuthenticationTokenMapper,
    @Inject private val authConfig: AuthConfig
) {

    /**
     * Generate a new access token using the information stored in a [authorizeAttempt].
     */
    suspend fun generateAccessToken(
        authorizeAttempt: AuthorizeAttempt,
        userId: UUID
    ) = generateAccessToken(
        userId = userId,
        clientId = authorizeAttempt.clientId,
        scopeTokens = authorizeAttempt.scopeTokens,
        authorizeAttemptId = authorizeAttempt.id
    )

    /**
     * Generate a new access token using the information stored in a [refreshToken].
     */
    suspend fun generateAccessToken(
        refreshToken: AuthenticationToken
    ) = generateAccessToken(
        userId = refreshToken.userId,
        clientId = refreshToken.clientId,
        scopeTokens = refreshToken.scopeTokens,
        authorizeAttemptId = refreshToken.authorizeAttemptId
    )

    internal suspend fun generateAccessToken(
        userId: UUID,
        clientId: String,
        scopeTokens: List<String>,
        authorizeAttemptId: UUID
    ): EncodedAuthenticationToken {
        val enabledAuthConfig = authConfig.orThrow()

        val issueDate = LocalDateTime.now()
        val expirationDate = issueDate.plus(enabledAuthConfig.token.accessExpiration)
        val entity = AuthenticationTokenEntity(
            userId = userId,
            type = AuthenticationTokenType.ACCESS.name,
            clientId = clientId,
            scopeTokens = scopeTokens.toTypedArray(),
            authorizeAttemptId = authorizeAttemptId,
            revoked = false,
            issueDate = issueDate,
            expirationDate = expirationDate
        ).let { tokenRepository.save(it) }

        val encodedToken = jwtManager.create(JwtManager.PUBLIC_KEY) {
            entity.id?.toString()?.let(this::withJWTId)
            enabledAuthConfig.audience?.let { this.withAudience(it) }
            withSubject(userId.toString())
            withIssuedAt(issueDate.toInstant(ZoneOffset.UTC))
            withExpiresAt(expirationDate.toInstant(ZoneOffset.UTC))
        }

        return tokenMapper.toEncodedAuthenticationToken(entity, encodedToken)
    }
}
