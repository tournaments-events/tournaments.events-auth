package tournament.events.auth.business.model.jwt

import com.auth0.jwt.algorithms.Algorithm
import io.micronaut.http.HttpStatus.BAD_REQUEST
import tournament.events.auth.business.exception.BusinessException

/**
 * Enumeration of all JWT signing algorithm supported by the project.
 *
 * This list is based on the algorithm supported by the (java-jwt)[https://github.com/auth0/java-jwt] library.
 */
enum class JwtAlgorithm(
    val behavior: JwtAlgorithmBehavior
) {
    RS256(RS256())
}

sealed class JwtAlgorithmBehavior {

    /**
     * Generate random signing keys.
     */
    fun generate(): JwtKeys {
        TODO()
    }

    /**
     * Initialize the [Algorithm] with the signing keys contained in [jwtKeys].
     */
    fun initializeWithKeys(jwtKeys: JwtKeys): Algorithm {
        return try {
            unsafetoAlgorithm(jwtKeys)
        } catch (t: Throwable) {
            throw BusinessException(
                status = BAD_REQUEST,
                messageId = "exception.jwt.invalid_key",
                throwable = t
            )
        }
    }

    protected abstract fun unsafetoAlgorithm(jwtKeys: JwtKeys): Algorithm
}

class RS256 : JwtAlgorithmBehavior() {
    override fun unsafetoAlgorithm(jwtKeys: JwtKeys): Algorithm {
        TODO("Not yet implemented")
    }
};
