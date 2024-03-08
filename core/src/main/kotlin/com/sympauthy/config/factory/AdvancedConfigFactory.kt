package com.sympauthy.config.factory

import com.sympauthy.business.manager.jwt.CryptoKeysGenerationStrategy
import com.sympauthy.business.model.jwt.JwtAlgorithm
import com.sympauthy.business.model.jwt.JwtAlgorithm.RS256
import com.sympauthy.business.model.user.UserMergingStrategy.BY_MAIL
import com.sympauthy.config.ConfigParser
import com.sympauthy.config.exception.ConfigurationException
import com.sympauthy.config.exception.configExceptionOf
import com.sympauthy.config.model.AdvancedConfig
import com.sympauthy.config.model.DisabledAdvancedConfig
import com.sympauthy.config.model.EnabledAdvancedConfig
import com.sympauthy.config.model.HashConfig
import com.sympauthy.config.properties.AdvancedConfigurationProperties
import com.sympauthy.config.properties.AdvancedConfigurationProperties.Companion.ADVANCED_KEY
import com.sympauthy.config.properties.HashConfigurationProperties
import com.sympauthy.config.properties.HashConfigurationProperties.Companion.HASH_KEY
import com.sympauthy.config.properties.JwtConfigurationProperties
import com.sympauthy.config.properties.JwtConfigurationProperties.Companion.JWT_KEY
import io.micronaut.context.annotation.Factory
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Factory
class AdvancedConfigFactory(
    @Inject private val parser: ConfigParser,
) {

    @Singleton
    fun provideConfig(
        properties: AdvancedConfigurationProperties,
        jwtProperties: JwtConfigurationProperties,
        hashProperties: HashConfigurationProperties,
        keyGenerationStategies: Map<String, CryptoKeysGenerationStrategy>,
    ): AdvancedConfig {
        val errors = mutableListOf<ConfigurationException>()
        val userMergingStrategy = try {
            parser.getEnum(
                properties, "$ADVANCED_KEY.user-merging-strategy", BY_MAIL,
                AdvancedConfigurationProperties::userMergingStrategy
            )
        } catch (e: ConfigurationException) {
            errors.add(e)
            null
        }

        val keysGenerationStrategy = try {
            getKeysGenerationStrategy(properties, keyGenerationStategies)
        } catch (e: ConfigurationException) {
            errors.add(e)
            null
        }

        val publicJwtAlgorithm = try {
            getPublicKeyAlgorithm(jwtProperties)
        } catch (e: ConfigurationException) {
            errors.add(e)
            null
        }

        val privateJwtAlgorithm = try {
            parser.getEnum(
                jwtProperties, "$JWT_KEY.private-alg", RS256,
                JwtConfigurationProperties::privateAlg
            )
        } catch (e: ConfigurationException) {
            errors.add(e)
            null
        }

        val (hashConfig, hashErrors) = getHashConfig(hashProperties)
        errors.addAll(hashErrors)

        return if (errors.isEmpty()) {
            return EnabledAdvancedConfig(
                userMergingStrategy = userMergingStrategy!!,
                keysGenerationStrategy = keysGenerationStrategy!!,
                publicJwtAlgorithm = publicJwtAlgorithm!!,
                privateJwtAlgorithm = privateJwtAlgorithm!!,
                hashConfig = hashConfig!!
            )
        } else {
            DisabledAdvancedConfig(errors)
        }
    }

    private fun getKeysGenerationStrategy(
        properties: AdvancedConfigurationProperties,
        keyGenerationStategies: Map<String, CryptoKeysGenerationStrategy>
    ): CryptoKeysGenerationStrategy {
        val strategyId = parser.getString(
            properties, "$ADVANCED_KEY.keys-generation-strategy",
            AdvancedConfigurationProperties::keysGenerationStrategy
        ) ?: DEFAULT_KEYS_GENERATION_ALGORITHM
        return keyGenerationStategies[strategyId] ?: throw configExceptionOf(
            "$ADVANCED_KEY.keys-generation-strategy",
            "config.unsupported_generation_algorithm",
            "algorithm" to strategyId,
            "algorithms" to keyGenerationStategies.keys.joinToString(", ")
        )
    }

    private fun getPublicKeyAlgorithm(
        properties: JwtConfigurationProperties
    ): JwtAlgorithm {
        val publicJwtAlgorithm = parser.getEnum(
            properties, "$JWT_KEY.public-alg", RS256,
            JwtConfigurationProperties::publicAlg
        )
        if (!publicJwtAlgorithm.keyAlgorithm.supportsPublicKey) {
            throw configExceptionOf(
                "$JWT_KEY.public-alg",
                "config.advanced.jwt.public_alg.unsupported_public_key",
                "algorithms" to JwtAlgorithm.entries
                    .filter { it.keyAlgorithm.supportsPublicKey }
                    .joinToString(", ")
            )
        }
        return publicJwtAlgorithm
    }

    private fun getHashConfig(
        properties: HashConfigurationProperties
    ): Pair<HashConfig?, List<ConfigurationException>> {
        val errors = mutableListOf<ConfigurationException>()

        val costParameter = try {
            getCostParameter(properties)
        } catch (e: ConfigurationException) {
            errors.add(e)
            null
        }

        val blockSize = try {
            getBlockSize(properties)
        } catch (e: ConfigurationException) {
            errors.add(e)
            null
        }

        val parallelizationParameter = try {
            getParallelizationParameter(properties, costParameter)
        } catch (e: ConfigurationException) {
            errors.add(e)
            null
        }

        val saltLengthInBytes = try {
            getSaltLengthInBytes(properties)
        } catch (e: ConfigurationException) {
            errors.add(e)
            null
        }

        val keyLengthInBytes = try {
            getKeyLengthInBytes(properties)
        } catch (e: ConfigurationException) {
            errors.add(e)
            null
        }

        return if (errors.isEmpty()) {
            val hashConfig = HashConfig(
                costParameter = costParameter!!,
                blockSize = blockSize!!,
                parallelizationParameter = parallelizationParameter!!,
                saltLengthInBytes = saltLengthInBytes!!,
                keyLengthInBytes = keyLengthInBytes!!
            )
            return hashConfig to emptyList()
        } else {
            return null to errors
        }
    }

    private fun getCostParameter(properties: HashConfigurationProperties): Int {
        val key = "$HASH_KEY.cost-parameter"
        val costParameter = parser.getInt(
            properties, key,
            HashConfigurationProperties::costParameter
        ) ?: DEFAULT_COST_PARAMETER
        if (costParameter !in 2..65535 || !isPowerOf2(costParameter)) {
            throw configExceptionOf(key, "config.advanced.hash.invalid_cost_parameter")
        }
        return costParameter
    }

    private fun getBlockSize(properties: HashConfigurationProperties): Int {
        val key = "$HASH_KEY.block-size"
        val blockSize = parser.getInt(
            properties, key,
            HashConfigurationProperties::blockSize
        ) ?: DEFAULT_BLOCK_SIZE
        if (blockSize <= 0) {
            throw configExceptionOf(key, "config.advanced.hash.invalid_block_size")
        }
        return blockSize
    }

    private fun getParallelizationParameter(
        properties: HashConfigurationProperties,
        costParameter: Int?
    ): Int? {
        if (costParameter == null) {
            return null
        }

        val key = "$HASH_KEY.parallelization-parameter"
        val max = Int.MAX_VALUE / (128 * costParameter * 8)
        val parallelizationParameter = parser.getInt(
            properties, key,
            HashConfigurationProperties::parallelizationParameter
        ) ?: DEFAULT_PARALLELIZATION_PARAMETER
        if (parallelizationParameter !in 1..max) {
            throw configExceptionOf(
                key, "config.advanced.hash.invalid_parallelization_parameter",
                "max" to max
            )
        }
        return parallelizationParameter
    }

    private fun getSaltLengthInBytes(properties: HashConfigurationProperties): Int {
        val key = "$HASH_KEY.salt-length"
        val saltLength = parser.getInt(
            properties, key,
            HashConfigurationProperties::saltLength
        ) ?: DEFAULT_SALT_LENGTH
        if (saltLength <= 0 && saltLength % 8 != 0) {
            throw configExceptionOf(key, "config.advanced.hash.invalid_salt_length")
        }
        return saltLength / 8
    }

    private fun getKeyLengthInBytes(properties: HashConfigurationProperties): Int {
        val key = "$HASH_KEY.key-length"
        val keyLength = parser.getInt(
            properties, key,
            HashConfigurationProperties::keyLength
        ) ?: DEFAULT_KEY_LENGTH
        if (keyLength <= 0) {
            throw configExceptionOf(key, "config.advanced.hash.invalid_key_length")
        }
        return keyLength
    }

    private fun isPowerOf2(var0: Int): Boolean = (var0 and var0 - 1) == 0

    companion object {
        private const val DEFAULT_KEYS_GENERATION_ALGORITHM = "autoincrement"
        private const val DEFAULT_COST_PARAMETER = 16_384
        private const val DEFAULT_BLOCK_SIZE = 8
        private const val DEFAULT_PARALLELIZATION_PARAMETER = 1
        private const val DEFAULT_SALT_LENGTH = 256
        private const val DEFAULT_KEY_LENGTH = 32
    }
}
