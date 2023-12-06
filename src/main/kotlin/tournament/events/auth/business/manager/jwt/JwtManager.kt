package tournament.events.auth.business.manager.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.business.exception.orMissingConfig
import tournament.events.auth.config.model.JwtConfig

/**
 * Manager in charge of all JSON web token issued by this server, it includes:
 * - creating the JWT.
 * - parsing JWT and verifying the signature.
 */
@Singleton
class JwtManager(
    @Inject private val keyManager: KeyManager,
    @Inject private val jwtConfig: JwtConfig
) {

    suspend fun create(
        name: String,
        block: JWTCreator.Builder.() -> JWTCreator.Builder = { this }
    ): String {
        val algorithm = keyManager.getAlgorithm(name)

        val builder = JWT.create()
            .withIssuer(jwtConfig.issuer.orMissingConfig("jwt.issuer"))

        return block(builder).sign(algorithm)
    }
}
