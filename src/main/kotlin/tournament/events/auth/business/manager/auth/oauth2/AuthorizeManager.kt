package tournament.events.auth.business.manager.auth.oauth2

import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.api.exception.oauth2ExceptionOf
import tournament.events.auth.business.manager.jwt.JwtManager
import tournament.events.auth.business.mapper.AuthorizeAttemptMapper
import tournament.events.auth.business.model.oauth2.AuthorizeAttempt
import tournament.events.auth.business.model.oauth2.OAuth2ErrorCode.INVALID_REQUEST
import tournament.events.auth.business.model.oauth2.OAuth2ErrorCode.SERVER_ERROR
import tournament.events.auth.data.model.AuthorizeAttemptEntity
import tournament.events.auth.data.repository.AuthorizeAttemptRepository
import tournament.events.auth.util.toAbsoluteUri
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
        clientId: String,
        uncheckedRedirectUri: String?,
        clientState: String? = null,
    ): AuthorizeAttempt {
        val redirectUri = checkRedirectUri(uncheckedRedirectUri)
        checkIsExistingAttemptWithState(clientState)

        // TODO: check client and redirect uri

        val entity = AuthorizeAttemptEntity(
            clientId = clientId,
            redirectUri = redirectUri.toString(),
            state = clientState,

            attemptDate = LocalDateTime.now(),
            expirationDate = LocalDateTime.now().plusMinutes(15) // TODO:
        )
        authorizeAttemptRepository.save(entity)

        return authorizeAttemptMapper.toAuthorizeAttempt(entity)
    }

    internal suspend fun checkIsExistingAttemptWithState(
        state: String?
    ) {
        val existingAttempt = state?.let {
            authorizeAttemptRepository.findByState(it)
        }
        if (existingAttempt != null) {
            throw oauth2ExceptionOf(INVALID_REQUEST, "description.oauth2.replay", "authorize.existing_state")
        }
    }

    internal fun checkRedirectUri(redirectUri: String?): URI {
        return redirectUri.toAbsoluteUri() ?: throw oauth2ExceptionOf(INVALID_REQUEST, "authorize.invalid_redirect_uri")
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
        // If the attempt is missing in DB, most likely a cron cleaned it up since it was expired.
        val authorizeAttempt = authorizeAttemptRepository.findById(attemptId)
            ?.let(authorizeAttemptMapper::toAuthorizeAttempt)
        if (authorizeAttempt == null || authorizeAttempt.expired) {
            throw oauth2ExceptionOf(
                SERVER_ERROR, "authorize.state.expired", "description.oauth2.expired"
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
