package com.sympauthy.business.manager.auth.oauth2

import com.auth0.jwt.JWTCreator
import com.sympauthy.business.manager.jwt.JwtManager
import com.sympauthy.business.manager.user.CollectedClaimManager
import com.sympauthy.business.mapper.EncodedAuthenticationTokenMapper
import com.sympauthy.business.model.oauth2.AuthenticationToken
import com.sympauthy.business.model.oauth2.AuthenticationTokenType
import com.sympauthy.business.model.oauth2.AuthorizeAttempt
import com.sympauthy.business.model.oauth2.EncodedAuthenticationToken
import com.sympauthy.business.model.user.CollectedClaim
import com.sympauthy.business.model.user.StandardScope
import com.sympauthy.config.model.AuthConfig
import com.sympauthy.config.model.orThrow
import com.sympauthy.data.model.AuthenticationTokenEntity
import com.sympauthy.data.repository.AuthenticationTokenRepository
import com.sympauthy.util.loggerForClass
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@Singleton
class IdTokenGenerator(
    @Inject private val collectedClaimManager: CollectedClaimManager,
    @Inject private val jwtManager: JwtManager,
    @Inject private val tokenRepository: AuthenticationTokenRepository,
    @Inject private val tokenMapper: EncodedAuthenticationTokenMapper,
    @Inject private val uncheckedAuthConfig: AuthConfig
) {

    private val logger = loggerForClass()

    /**
     * Generate a new id token containing user info accessible according to the scopes granted in [authorizeAttempt].
     */
    suspend fun generateIdToken(
        authorizeAttempt: AuthorizeAttempt,
        userId: UUID,
        accessToken: EncodedAuthenticationToken
    ) = generateIdToken(
        userId = userId,
        clientId = authorizeAttempt.clientId,
        scopes = authorizeAttempt.grantedScopes ?: emptyList(),
        authorizeAttemptId = authorizeAttempt.id,
        accessToken = accessToken
    )

    /**
     * Generate a new access token using the information stored in a [refreshToken].
     */
    suspend fun generateIdToken(
        refreshToken: AuthenticationToken,
        accessToken: EncodedAuthenticationToken
    ) = generateIdToken(
        userId = refreshToken.userId,
        clientId = refreshToken.clientId,
        scopes = refreshToken.scopes,
        authorizeAttemptId = refreshToken.authorizeAttemptId,
        accessToken = accessToken
    )

    internal suspend fun generateIdToken(
        userId: UUID,
        clientId: String,
        scopes: List<String>,
        authorizeAttemptId: UUID,
        accessToken: EncodedAuthenticationToken
    ): EncodedAuthenticationToken? {
        val authConfig = uncheckedAuthConfig.orThrow()


        // FIXME compute at_hash with accessToken

        val claims = collectedClaimManager.findReadableUserInfoByUserId(
            userId = userId,
            scopes = scopes
        )

        val issueDate = LocalDateTime.now()
        val expirationDate = issueDate.plus(authConfig.token.idExpiration)
        val entity = AuthenticationTokenEntity(
            userId = userId,
            type = AuthenticationTokenType.ID.name,
            clientId = clientId,
            scopes = scopes.toTypedArray(),
            authorizeAttemptId = authorizeAttemptId,
            revoked = false,
            issueDate = issueDate,
            expirationDate = expirationDate
        ).let { tokenRepository.save(it) }

        val encodedToken = jwtManager.create(JwtManager.PUBLIC_KEY) {
            entity.id?.toString()?.let(this::withJWTId)
            authConfig.audience?.let { this.withAudience(it) }
            withSubject(userId.toString())
            withIssuedAt(issueDate.toInstant(ZoneOffset.UTC))
            withExpiresAt(expirationDate.toInstant(ZoneOffset.UTC))

            claims.forEach { claim ->
                withClaim(claim)
            }
        }

        return tokenMapper.toEncodedAuthenticationToken(entity, encodedToken)
    }

    internal fun shouldGenerateIdToken(scopes: List<String>): Boolean {
        return scopes.contains(StandardScope.OPENID.scope)
    }

    private fun JWTCreator.Builder.withClaim(claim: CollectedClaim) {
        when (claim.value) { // FIXME add other types
            is String -> withClaim(claim.claim.id, claim.value)
            else -> {
                logger.error("Unable to encode claim '${claim.claim.id}' into id token.")
            }
        }
        if (claim.claim.verifiedId != null) {
            withClaim(claim.claim.verifiedId, claim.verified ?: false)
        }
    }
}
