package tournament.events.auth.business.manager.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.business.manager.key.CryptoKeysManager
import tournament.events.auth.config.model.AdvancedConfig
import tournament.events.auth.config.model.AuthConfig
import tournament.events.auth.config.model.orThrow

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
        block: JWTCreator.Builder.() -> JWTCreator.Builder = { this }
    ): String {
        val algorithm = getAlgorithm(name)
        return JWT.create().apply {
            authConfig.orThrow().issuer?.let(this::withIssuer)
            block(this)
        }.sign(algorithm)
    }

    suspend fun decodeAndVerify(
        name: String,
        token: String
    ): DecodedJWT? {
        val decodedJwt = try {
            JWT.decode(token)
        } catch (e: JWTDecodeException) {
            return null
        }

        val algorithm = getAlgorithm(name)
        return try {
            JWT.require(algorithm)
                .build()
                .verify(decodedJwt)
            decodedJwt
        } catch (e: JWTVerificationException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Return the [Algorithm] initialized with the signing key named [name].
     *
     * If the key does not exist in the database or has not been configured in the application.yml,
     * then it will be generated according to the key generation strategy.
     */
    suspend fun getAlgorithm(name: String): Algorithm {
        val algorithm = advancedConfig.orThrow().jwtAlgorithm
        val key = keyManager.getKey(name, algorithm.keyAlgorithm)
        return algorithm.impl.initializeWithKeys(key)
    }
}
