package tournament.events.auth.business.model.jwt

import com.auth0.jwt.algorithms.Algorithm
import io.micronaut.http.HttpStatus.INTERNAL_SERVER_ERROR
import tournament.events.auth.business.exception.BusinessException
import tournament.events.auth.business.model.key.KeyAlgorithm
import tournament.events.auth.business.model.key.KeyAlgorithm.RSA
import tournament.events.auth.business.model.key.CryptoKeys
import tournament.events.auth.business.model.key.RSAKeyImpl
import tournament.events.auth.business.model.key.getImpl

/**
 * Enumeration of all JWT signing algorithm supported by the project.
 *
 * This list is based on the algorithm supported by the (java-jwt)[https://github.com/auth0/java-jwt] library.
 */
enum class JwtAlgorithm(
    /**
     * Cryptographic algorithm used to sign.
     */
    val keyAlgorithm: KeyAlgorithm,
    val impl: JwtAlgorithmImpl
) {
    RS256(RSA, RS256AlgorithmImpl())
}

sealed class JwtAlgorithmImpl {

    /**
     * Initialize the [Algorithm] with the signing keys contained in [cryptoKeys].
     */
    fun initializeWithKeys(cryptoKeys: CryptoKeys): Algorithm {

        return try {
            unsafetoAlgorithm(cryptoKeys)
        } catch (e: BusinessException) {
            throw e
        } catch (t: Throwable) {
            throw BusinessException(
                status = INTERNAL_SERVER_ERROR,
                messageId = "exception.jwt.invalid_key",
                values = mapOf(
                    "name" to cryptoKeys.name
                ),
                throwable = t
            )
        }
    }

    protected abstract fun unsafetoAlgorithm(cryptoKeys: CryptoKeys): Algorithm
}

class RS256AlgorithmImpl : JwtAlgorithmImpl() {

    override fun unsafetoAlgorithm(cryptoKeys: CryptoKeys): Algorithm {
        val privateKey = RSA.getImpl<RSAKeyImpl>()
            .toPrivateKey(cryptoKeys)
        return Algorithm.RSA256(privateKey)
    }
}
