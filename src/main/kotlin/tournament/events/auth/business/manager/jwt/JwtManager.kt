package tournament.events.auth.business.manager.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.DecodedJWT
import com.nimbusds.jose.jwk.JWK
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.business.manager.key.CryptoKeysManager
import tournament.events.auth.config.model.AdvancedConfig
import tournament.events.auth.config.model.AuthConfig
import tournament.events.auth.config.model.orThrow
import tournament.events.auth.exception.LocalizedException

/**
 * Manager in charge of all JSON web token issued by this server, it includes:
 * - creating the JWT.
 * - parsing JWT and verifying the signature.
 */
@Singleton
class JwtManager(
    @Inject private val keyManager: CryptoKeysManager,
    @Inject private val advancedConfig: AdvancedConfig,
    @Inject private val authConfig: AuthConfig,
) {

    suspend fun create(
        name: String,
        block: JWTCreator.Builder.() -> Unit = { this }
    ): String {
        val algorithm = getAlgorithm(name)
        return JWT.create().apply {
            authConfig.orThrow().issuer?.let(this::withIssuer)
            block(this)
            this
        }.sign(algorithm)
    }

    /**
     * Decode the [token] and perform validation on it:
     * - if the token has not been signed by the key with [name].
     * – if the token has expired.
     *
     * @throws LocalizedException if the [token] is malformed or invalid.
     */
    suspend fun decodeAndVerify(
        name: String,
        token: String
    ): DecodedJWT {
        val decodedJwt = try {
            JWT.decode(token)
        } catch (e: JWTDecodeException) {
            throw LocalizedException(
                detailsId = "jwt.malformed",
                throwable = e
            )
        }

        val algorithm = getAlgorithm(name)
        return try {
            JWT.require(algorithm)
                .build()
                .verify(decodedJwt)
            decodedJwt
        } catch (e: TokenExpiredException) {
            throw LocalizedException(
                detailsId = "jwt.expired",
                throwable = e
            )
        } catch (e: JWTVerificationException) {
            throw LocalizedException(
                detailsId = "jwt.invalid_signature",
                throwable = e
            )
        }
    }

    suspend fun decodeAndVerifyOrNull(name: String, token: String) = try {
        decodeAndVerify(name, token)
    } catch (e: LocalizedException) {
        when (e.detailsId) {
            "jwt.expired", "jwt.malformed", "jwt.invalid_signature" -> null
            else -> throw e
        }
    }

    /**
     * Return the public key of the [PUBLIC_KEY] key set used to sign access tokens and id tokens.
     */
    suspend fun getPublicKey(): JWK {
        val algorithm = advancedConfig.orThrow().publicJwtAlgorithm
        val keys = keyManager.getKey(PUBLIC_KEY, algorithm.keyAlgorithm)
        return algorithm.keyAlgorithm.impl.serializePublicKey(keys)
    }

    /**
     * Return the [Algorithm] initialized with the signing key named [name].
     *
     * If the key does not exist in the database or has not been configured in the application.yml,
     * then it will be generated according to the key generation strategy.
     */
    suspend fun getAlgorithm(name: String): Algorithm {
        val algorithm = when(name) {
            PUBLIC_KEY -> advancedConfig.orThrow().publicJwtAlgorithm
            else -> advancedConfig.orThrow().privateJwtAlgorithm
        }
        val key = keyManager.getKey(name, algorithm.keyAlgorithm)
        return algorithm.impl.initializeWithKeys(key)
    }

    companion object {
        /**
         * Name of the public key used to sign access tokens and id tokens.
         */
        const val PUBLIC_KEY = "public"

        /**
         * Name of key used to sign refresh tokens.
         * Unlike the one for access and id tokens, this one is kept secret.
         */
        const val REFRESH_KEY = "refresh"
    }
}
