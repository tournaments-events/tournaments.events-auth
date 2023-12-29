package tournament.events.auth.config.factory

import io.micronaut.context.annotation.Factory
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.business.manager.jwt.CryptoKeysGenerationStrategy
import tournament.events.auth.business.model.jwt.JwtAlgorithm
import tournament.events.auth.business.model.jwt.JwtAlgorithm.RS256
import tournament.events.auth.business.model.user.UserMergingStrategy
import tournament.events.auth.config.ConfigParser
import tournament.events.auth.config.exception.ConfigurationException
import tournament.events.auth.config.exception.configExceptionOf
import tournament.events.auth.config.model.AdvancedConfig
import tournament.events.auth.config.model.DisabledAdvancedConfig
import tournament.events.auth.config.model.EnabledAdvancedConfig
import tournament.events.auth.config.properties.AdvancedConfigurationProperties
import tournament.events.auth.config.properties.AdvancedConfigurationProperties.Companion.ADVANCED_KEY
import tournament.events.auth.config.properties.JwtConfigurationProperties
import tournament.events.auth.config.properties.JwtConfigurationProperties.Companion.JWT_KEY

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
                properties, "$ADVANCED_KEY.user-merging-strategy", UserMergingStrategy.BY_MAIL,
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
                "algorithms" to JwtAlgorithm.values().filter { it.keyAlgorithm.supportsPublicKey }.joinToString(", ")
            )
        }
        return publicJwtAlgorithm
    }
}
