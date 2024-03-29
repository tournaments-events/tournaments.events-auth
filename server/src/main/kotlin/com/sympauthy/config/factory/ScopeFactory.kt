package com.sympauthy.config.factory

import com.sympauthy.business.model.user.isStandardScope
import com.sympauthy.config.ConfigParser
import com.sympauthy.config.exception.ConfigurationException
import com.sympauthy.config.model.*
import com.sympauthy.config.properties.ScopeConfigurationProperties
import com.sympauthy.config.properties.ScopeConfigurationProperties.Companion.SCOPES_KEY
import io.micronaut.context.annotation.Factory
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Factory
class ScopeFactory(
    @Inject private val parser: ConfigParser
) {

    @Singleton
    fun provideScopes(configs: List<ScopeConfigurationProperties>): ScopesConfig {
        val errors = mutableListOf<ConfigurationException>()
        val scopes = configs.mapNotNull { config ->
            if (config.id.isStandardScope()) {
                getStandardScope(config = config, errors = errors)
            } else {
                getCustomScope(config = config, errors = errors)
            }
        }

        return if (errors.isEmpty()) {
            EnabledScopesConfig(scopes)
        } else {
            DisabledScopesConfig(errors)
        }
    }

    private fun getStandardScope(
        config: ScopeConfigurationProperties,
        errors: MutableList<ConfigurationException>
    ): ScopeConfig? {
        val scopeErrors = mutableListOf<ConfigurationException>()

        val enabled = try {
            parser.getBoolean(
                config, "$SCOPES_KEY.${config.id}.enabled",
                ScopeConfigurationProperties::enabled
            ) ?: true
        } catch (e: ConfigurationException) {
            scopeErrors.add(e)
            null
        }

        return if (scopeErrors.isEmpty()) {
            StandardScopeConfig(
                scope = config.id,
                enabled = enabled!!
            )
        } else {
            errors.addAll(scopeErrors)
            null
        }
    }

    private fun getCustomScope(
        config: ScopeConfigurationProperties,
        errors: MutableList<ConfigurationException>
    ): ScopeConfig? {
        val scopeErrors = mutableListOf<ConfigurationException>()

        return if (scopeErrors.isEmpty()) {
            CustomScopeConfig(
                scope = config.id
            )
        } else {
            errors.addAll(scopeErrors)
            null
        }
    }
}
