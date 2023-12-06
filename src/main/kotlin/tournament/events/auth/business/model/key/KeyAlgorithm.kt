package tournament.events.auth.business.model.key

import io.micronaut.http.HttpStatus.INTERNAL_SERVER_ERROR
import tournament.events.auth.business.exception.businessExceptionOf
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.KeySpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

enum class KeyAlgorithm(
    val impl: KeyAlgorithmImpl
) {
    RSA(RSAKeyImpl())
}

inline fun <reified T: KeyAlgorithmImpl> KeyAlgorithm.getImpl(): T {
    return this.impl as T
}

sealed class KeyAlgorithmImpl {

    /**
     * Generate random keys.
     */
    abstract fun generate(name: String): CryptoKeys
}

class RSAKeyImpl : KeyAlgorithmImpl() {

    override fun generate(name: String): CryptoKeys {
        val keyPair = KeyPairGenerator.getInstance("RSA").apply {
            initialize(2048)
        }.generateKeyPair()

        return CryptoKeys(
            name = name,
            algorithm = "RSA",
            publicKey = keyPair.public.encoded,
            publicKeyFormat = keyPair.public.format,
            privateKey = keyPair.private.encoded,
            privateKeyFormat = keyPair.private.format
        )
    }

    fun toPublicKey(keys: CryptoKeys): RSAPublicKey {
        if (keys.publicKey == null || keys.publicKeyFormat == null) {
            throw businessExceptionOf(
                INTERNAL_SERVER_ERROR, "exception.key.missing_public_key",
                "name" to keys.name
            )
        }
        val keySpec = getKeySpec(
            name = keys.name,
            key = keys.publicKey,
            format = keys.publicKeyFormat
        )
        val factory = KeyFactory.getInstance("RSA")
        return factory.generatePublic(keySpec) as RSAPublicKey
    }

    fun toPrivateKey(keys: CryptoKeys): RSAPrivateKey {
        val keySpec = getKeySpec(
            name = keys.name,
            key = keys.privateKey,
            format = keys.privateKeyFormat
        )
        val factory = KeyFactory.getInstance("RSA")
        return factory.generatePrivate(keySpec) as RSAPrivateKey
    }
}

fun getKeySpec(
    name: String,
    key: ByteArray,
    format: String
): KeySpec {
    return when (format) {
        "PKCS#8" -> PKCS8EncodedKeySpec(key)
        "X.509" -> X509EncodedKeySpec(key)
        else -> throw businessExceptionOf(
            INTERNAL_SERVER_ERROR, "exception.key.unsupported_key_spec",
            "name" to name,
            "keySpec" to format
        )
    }
}
