package com.sympauthy.business.manager

import jakarta.inject.Singleton
import java.security.SecureRandom
import java.util.*

/**
 * Generate randomized payload to be used across the application.
 */
@Singleton
class RandomGenerator {

    private val secureRandom: SecureRandom = SecureRandom()
    private val encoder: Base64.Encoder = Base64.getEncoder()

    /**
     * Generate a byte array containing random values.
     */
    fun generate(lengthInBytes: Int = DEFAULT_LENGTH): ByteArray {
        if (lengthInBytes <= 0) {
            throw IllegalArgumentException("lengthInBytes must be >= 1.")
        }
        val byteArray = ByteArray(lengthInBytes)
        secureRandom.nextBytes(byteArray)
        return byteArray
    }

    fun generateAndEncodeToBase64(lengthInBytes: Int = DEFAULT_LENGTH): String {
        return encoder.encodeToString(generate(lengthInBytes))
    }

    companion object {
        const val DEFAULT_LENGTH = 8
    }
}
