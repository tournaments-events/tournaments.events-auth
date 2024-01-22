package com.sympauthy.business.manager.key

import jakarta.inject.Singleton
import java.security.SecureRandom
import java.util.*

/**
 * Generate randomized keys to be used across the application.
 */
@Singleton
class RandomKeyGenerator {

    private val secureRandom: SecureRandom = SecureRandom()
    private val encoder: Base64.Encoder = Base64.getEncoder()

    fun generateKey(keyLength: Int = DEFAULT_KEY_LENGTH): String {
        val byteArray = ByteArray(keyLength.floorDiv(8))
        secureRandom.nextBytes(byteArray)
        return encoder.encodeToString(byteArray)
    }

    companion object {
        const val DEFAULT_KEY_LENGTH = 64
    }
}
