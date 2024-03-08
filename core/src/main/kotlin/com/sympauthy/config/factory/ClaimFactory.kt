package com.sympauthy.config.factory

import com.sympauthy.business.model.user.UserMergingStrategy.BY_MAIL
import com.sympauthy.business.model.user.claim.Claim
import com.sympauthy.business.model.user.claim.OpenIdClaim
import com.sympauthy.business.model.user.claim.OpenIdClaim.Id.EMAIL
import com.sympauthy.business.model.user.claim.StandardClaim
import com.sympauthy.config.ConfigParser
import com.sympauthy.config.exception.ConfigurationException
import com.sympauthy.config.exception.configExceptionOf
import com.sympauthy.config.model.*
import com.sympauthy.config.properties.ClaimConfigurationProperties
import com.sympauthy.config.properties.ClaimConfigurationProperties.Companion.CLAIMS_KEY
import io.micronaut.context.annotation.Factory
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Factory
class ClaimFactory(
    @Inject private val parser: ConfigParser,
    @Inject private val uncheckedAdvancedConfig: AdvancedConfig
) {

    @Singleton
    fun provideClaims(properties: List<ClaimConfigurationProperties>): ClaimsConfig {
        val errors = mutableListOf<ConfigurationException>()

        val standardClaims = OpenIdClaim.entries.mapNotNull { openIdClaim ->
            try {
                provideStandardClaim(
                    properties = properties.firstOrNull { it.id == openIdClaim.id },
                    openIdClaim = openIdClaim
                )
            } catch (e: ConfigurationException) {
                errors.add(e)
                null
            }
        }

        val customClaims = properties.mapNotNull { claimProperties ->
            try {
                if (OpenIdClaim.entries.none { it.id == claimProperties.id }) {
                    provideCustomClaim(claimProperties)
                } else null
            } catch (e: ConfigurationException) {
                errors.add(e)
                null
            }
        }

        // Check if claims are properly configured for by-mail user merging strategy.
        // We ignore the check is the config is invalid since to avoid crashing the server.
        if (uncheckedAdvancedConfig is EnabledAdvancedConfig) {
            val emailClaim = standardClaims.firstOrNull { it.id == EMAIL }
            if (uncheckedAdvancedConfig.userMergingStrategy == BY_MAIL && emailClaim == null) {
                errors.add(
                    configExceptionOf(
                        "$CLAIMS_KEY.${EMAIL}",
                        "config.claim.email.disabled"
                    )
                )
            }
        }

        return if (errors.isEmpty()) {
            EnabledClaimsConfig(standardClaims + customClaims)
        } else {
            DisabledClaimsConfig(errors)
        }
    }

    private fun provideStandardClaim(
        properties: ClaimConfigurationProperties?,
        openIdClaim: OpenIdClaim
    ): Claim? {
        val enabled = properties?.let {
            parser.getBoolean(
                it, "$CLAIMS_KEY.${it.id}.enabled",
                ClaimConfigurationProperties::enabled
            )
        } ?: true
        val required = properties?.let {
            parser.getBoolean(
                it, "$CLAIMS_KEY.${it.id}.required",
                ClaimConfigurationProperties::required
            )
        } ?: false
        return if (enabled) {
            StandardClaim(
                openIdClaim = openIdClaim,
                required = required
            )
        } else null
    }

    private fun provideCustomClaim(properties: ClaimConfigurationProperties): Claim? {
        // FIXME Handle custom claims
        return null
    }
}
