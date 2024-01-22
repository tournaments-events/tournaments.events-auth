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
import com.sympauthy.config.properties.AdvancedConfigurationProperties
import com.sympauthy.config.properties.AdvancedConfigurationProperties.Companion.ADVANCED_KEY
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

        return if (errors.isEmpty()) {
            return EnabledAdvancedConfig(
                userMergingStrategy = userMergingStrategy!!,
                keysGenerationStrategy = keysGenerationStrategy!!,
                publicJwtAlgorithm = publicJwtAlgorithm!!,
                privateJwtAlgorithm = privateJwtAlgorithm!!
            )
        } else {
            DisabledAdvancedConfig(errors)
        }
    }

    private fun getKeysGenerationStrategy(
        properties: AdvancedConfigurationProperties,
        keyGenerationStategies: Map<String, CryptoKeysGenerationStrategy>
    ): CryptoKeysGenerationStrategy {
        val strategyId = parser.getStringOrThrow(
            properties, "$ADVANCED_KEY.keys-generation-strategy",
            AdvancedConfigurationProperties::keysGenerationStrategy
        )
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
}
