package com.sympauthy.business.manager.jwt

import com.sympauthy.business.model.key.CryptoKeys
import com.sympauthy.business.model.key.KeyAlgorithm

interface CryptoKeysGenerationStrategy {

    /**
     * Generate cryptographic keys usable by the [algorithm] and identified by [name].
     *
     * The generation strategy must support multiple instances being run in a cluster.
     */
    suspend fun generateKeys(name: String, algorithm: KeyAlgorithm): CryptoKeys
}
