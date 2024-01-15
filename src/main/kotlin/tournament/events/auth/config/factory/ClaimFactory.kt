package tournament.events.auth.config.factory

import io.micronaut.context.annotation.Factory
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.business.model.user.claim.Claim
import tournament.events.auth.business.model.user.claim.OpenIdClaim
import tournament.events.auth.business.model.user.claim.StandardClaim
import tournament.events.auth.config.ConfigParser
import tournament.events.auth.config.exception.ConfigurationException
import tournament.events.auth.config.model.ClaimsConfig
import tournament.events.auth.config.model.DisabledClaimsConfig
import tournament.events.auth.config.model.EnabledClaimsConfig
import tournament.events.auth.config.properties.ClaimConfigurationProperties
import tournament.events.auth.config.properties.ClaimConfigurationProperties.Companion.CLAIMS_KEY

@Factory
class ClaimFactory(
    @Inject private val parser: ConfigParser
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
        return if (enabled) StandardClaim(openIdClaim) else null
    }

    private fun provideCustomClaim(properties: ClaimConfigurationProperties): Claim? {
        // FIXME Handle custom claims
        return null
    }
}
