package com.sympauthy.business.manager.auth.oauth2

import com.sympauthy.api.exception.oauth2ExceptionOf
import com.sympauthy.business.manager.jwt.JwtManager
import com.sympauthy.business.mapper.AuthorizeAttemptMapper
import com.sympauthy.business.model.client.Client
import com.sympauthy.business.model.oauth2.AuthorizeAttempt
import com.sympauthy.business.model.oauth2.OAuth2ErrorCode.INVALID_REQUEST
import com.sympauthy.business.model.oauth2.Scope
import com.sympauthy.data.model.AuthorizeAttemptEntity
import com.sympauthy.data.repository.AuthorizeAttemptRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.net.URI
import java.time.LocalDateTime
import java.util.*

@Singleton
class AuthorizeManager(
    @Inject private val authorizeAttemptRepository: AuthorizeAttemptRepository,
    @Inject private val jwtManager: JwtManager,
    @Inject private val authorizeAttemptMapper: AuthorizeAttemptMapper
) {

    suspend fun newAuthorizeAttempt(
        client: Client,
        clientState: String? = null,
        uncheckedScopes: List<Scope>? = null,
        uncheckedRedirectUri: URI? = null,
    ): AuthorizeAttempt {
        val scopes = checkScopes(client, uncheckedScopes)
        val redirectUri = checkRedirectUri(client, uncheckedRedirectUri)
        checkIsExistingAttemptWithState(clientState)

        val entity = AuthorizeAttemptEntity(
            clientId = client.id,
            redirectUri = redirectUri.toString(),
            scopes = scopes.map(Scope::scope).toTypedArray(),
            state = clientState,

            attemptDate = LocalDateTime.now(),
            expirationDate = LocalDateTime.now().plusMinutes(15) // TODO: Add to advanced config
        )
        authorizeAttemptRepository.save(entity)

        return authorizeAttemptMapper.toAuthorizeAttempt(entity)
    }

    internal fun checkScopes(
        client: Client,
        uncheckedScopes: List<Scope>?
    ): List<Scope> {
        val scopes = uncheckedScopes ?: client.defaultScopes
        return if (scopes == null) {
            throw oauth2ExceptionOf(INVALID_REQUEST, "authorize.scope.missing", "description.oauth2.invalid")
        } else if (client.allowedScopes != null) {
            scopes.filter { client.allowedScopes.contains(it) }
        } else scopes
    }

    internal fun checkRedirectUri(
        client: Client,
        uncheckedRedirectUri: URI?
    ): URI {
        // TODO: check redirect uri is valid for client.
        return uncheckedRedirectUri!!
    }

    internal suspend fun checkIsExistingAttemptWithState(
        state: String?
    ) {
        val existingAttempt = state?.let {
            authorizeAttemptRepository.findByState(it)
        }
        if (existingAttempt != null) {
            throw oauth2ExceptionOf(INVALID_REQUEST, "authorize.existing_state", "description.oauth2.replay")
        }
    }

    suspend fun encodeState(authorizeAttempt: AuthorizeAttempt): String {
        return jwtManager.create(STATE_KEY_NAME) {
            withKeyId(STATE_KEY_NAME)
            withSubject(authorizeAttempt.id.toString())
        }
    }

    /**
     * Return the [AuthorizeAttempt] that created the [state] after verifying the [state] has not been tempered with.
     */
    suspend fun verifyEncodedState(state: String?): AuthorizeAttempt {
        if (state.isNullOrBlank()) {
            throw oauth2ExceptionOf(INVALID_REQUEST, "authorize.state.missing")
        }
        val jwt = jwtManager.decodeAndVerifyOrNull(STATE_KEY_NAME, state) ?: throw oauth2ExceptionOf(
            INVALID_REQUEST, "authorize.state.wrong_signature", "description.oauth2.invalid_state"
        )
        val attemptId = try {
            UUID.fromString(jwt.subject)
        } catch (e: IllegalArgumentException) {
            throw oauth2ExceptionOf(
                INVALID_REQUEST, "authorize.state.invalid_subject", "description.oauth2.invalid_state"
            )
        }
        val authorizeAttempt = authorizeAttemptRepository.findById(attemptId)
            ?.let(authorizeAttemptMapper::toAuthorizeAttempt)
        if (authorizeAttempt == null || authorizeAttempt.expired) {
            // If the attempt is missing in DB, most likely a cron cleaned it.
            throw oauth2ExceptionOf(
                INVALID_REQUEST, "authorize.state.expired", "description.oauth2.expired"
            )
        }
        return authorizeAttempt
    }

    /**
     * Associate the user that have been authenticated to its [AuthorizeAttempt].
     */
    suspend fun setAuthenticatedUserId(authorizeAttempt: AuthorizeAttempt, userId: UUID): AuthorizeAttempt {
        authorizeAttemptRepository.updateUserId(authorizeAttempt.id, userId)
        return authorizeAttempt.copy(
            userId = userId
        )
    }

    suspend fun findByCode(code: String): AuthorizeAttempt? {
        val authorizeAttempt = authorizeAttemptRepository.findByCode(code)
            ?.let(authorizeAttemptMapper::toAuthorizeAttempt)
        return if (authorizeAttempt?.expired == false) {
            authorizeAttempt
        } else null
    }

    companion object {
        /**
         * Name of the cryptographic key used to sign the state.
         */
        const val STATE_KEY_NAME = "state"
    }
}
