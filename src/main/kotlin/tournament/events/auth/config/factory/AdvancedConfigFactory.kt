package tournament.events.auth.config.factory

import io.micronaut.context.annotation.Factory
import io.micronaut.http.HttpStatus
import jakarta.inject.Singleton
import tournament.events.auth.business.exception.BusinessException
import tournament.events.auth.business.exception.businessExceptionOf
import tournament.events.auth.business.manager.jwt.CryptoKeysGenerationStrategy
import tournament.events.auth.business.model.jwt.JwtAlgorithm
import tournament.events.auth.config.model.AdvancedConfig
import tournament.events.auth.config.model.DisabledAdvancedConfig
import tournament.events.auth.config.model.EnabledAdvancedConfig
import tournament.events.auth.business.model.user.UserMergingStrategy
import tournament.events.auth.config.properties.AdvancedConfigurationProperties
import tournament.events.auth.config.properties.AdvancedConfigurationProperties.Companion.ADVANCED_CONFIG_KEY
import tournament.events.auth.config.util.getEnum
import tournament.events.auth.config.util.getStringOrThrow

@Factory
class AdvancedConfigFactory {

    @Singleton
    fun provideConfig(
        properties: AdvancedConfigurationProperties,
        keyGenerationStategies: Map<String, CryptoKeysGenerationStrategy>,
    ): AdvancedConfig {
        val errors = mutableListOf<BusinessException>()
        val userMergingStrategy = try {
            getEnum(
                properties, "$ADVANCED_CONFIG_KEY.user-merging-strategy",
                UserMergingStrategy.BY_MAIL, AdvancedConfigurationProperties::userMergingStrategy
            )
        } catch (e: BusinessException) {
            errors.add(e)
            null
        }

        val keysGenerationStrategy = try {
            getKeysGenerationStrategy(properties, keyGenerationStategies)
        } catch (e: BusinessException) {
            errors.add(e)
            null
        }

        val jwtAlgorithm = try {
            getEnum(
                properties, "$ADVANCED_CONFIG_KEY.jwt-algorithm",
                JwtAlgorithm.RS256, AdvancedConfigurationProperties::jwtAlgorithm
            )
        } catch (e: BusinessException) {
            errors.add(e)
            null
        }

        return if (errors.isEmpty()) {
            return EnabledAdvancedConfig(
                userMergingStrategy = userMergingStrategy!!,
                keysGenerationStrategy = keysGenerationStrategy!!,
                jwtAlgorithm = jwtAlgorithm!!
            )
        } else {
            DisabledAdvancedConfig(errors)
        }
    }

    private fun getKeysGenerationStrategy(
        properties: AdvancedConfigurationProperties,
        keyGenerationStategies: Map<String, CryptoKeysGenerationStrategy>
    ): CryptoKeysGenerationStrategy {
        val strategyId = getStringOrThrow(
            properties, "sympauthy.keys-generation-strategy"
        ) { it.keysGenerationStrategy }
        return keyGenerationStategies[strategyId] ?: throw businessExceptionOf(
            HttpStatus.INTERNAL_SERVER_ERROR, "config.unsupported_generation_algorithm",
            "key" to "sympauthy.keys-generation-strategy",
            "algorithm" to strategyId,
            "algorithms" to keyGenerationStategies.keys.joinToString(", ")
        )
    }
}
