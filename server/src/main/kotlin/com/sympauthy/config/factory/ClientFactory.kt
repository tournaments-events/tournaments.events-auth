package com.sympauthy.config.factory

import com.sympauthy.business.manager.ScopeManager
import com.sympauthy.business.model.client.Client
import com.sympauthy.business.model.oauth2.Scope
import com.sympauthy.config.ConfigParser
import com.sympauthy.config.exception.ConfigurationException
import com.sympauthy.config.exception.configExceptionOf
import com.sympauthy.config.model.ClientsConfig
import com.sympauthy.config.model.DisabledClientsConfig
import com.sympauthy.config.model.EnabledClientsConfig
import com.sympauthy.config.properties.ClientConfigurationProperties
import com.sympauthy.config.properties.ClientConfigurationProperties.Companion.CLIENTS_KEY
import com.sympauthy.config.properties.ClientConfigurationProperties.Companion.DEFAULT
import io.micronaut.context.annotation.Factory
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.URI

@Factory
class ClientFactory(
    @Inject private val parser: ConfigParser,
    @Inject private val scopeManager: ScopeManager
) {

    @Singleton
    fun provideClients(configs: List<ClientConfigurationProperties>): Flow<ClientsConfig> {
        return flow {
            val errors = mutableListOf<ConfigurationException>()

            val defaultConfig = configs.firstOrNull { it.id == DEFAULT }

            val clients = configs
                .filter { it.id != DEFAULT }
                .mapNotNull { config ->
                    getClient(config = config, defaultConfig = defaultConfig, errors = errors)
                }

            val config = if (errors.isEmpty()) {
                EnabledClientsConfig(clients)
            } else {
                DisabledClientsConfig(errors)
            }
            emit(config)
        }
    }

    private suspend fun getClient(
        config: ClientConfigurationProperties,
        defaultConfig: ClientConfigurationProperties?,
        errors: MutableList<ConfigurationException>
    ): Client? {
        val secret = try {
            parser.getString(
                config, "$CLIENTS_KEY.${config.id}.secret",
                ClientConfigurationProperties::secret
            )
        } catch (e: ConfigurationException) {
            errors.add(e)
            null
        }

        val allowedRedirectUris = getAllowedRedirectUris(
            config = config,
            allowedRedirectUris = config.allowedRedirectUris ?: defaultConfig?.allowedRedirectUris,
            errors = errors
        )

        val allowedScopes = getScopes(
            key = "$CLIENTS_KEY.${config.id}.allowed-scopes",
            scopes = config.allowedScopes ?: defaultConfig?.allowedScopes,
            errors = errors
        )?.toSet()

        val defaultScopes = getScopes(
            key = "$CLIENTS_KEY.${config.id}.default-scopes",
            scopes = config.defaultScopes ?: defaultConfig?.defaultScopes,
            errors = errors
        )

        return if (errors.isEmpty()) {
            return Client(
                id = config.id,
                secret = secret!!,
                allowedRedirectUris = allowedRedirectUris,
                allowedScopes = allowedScopes,
                defaultScopes = defaultScopes
            )
        } else null
    }

    private fun getAllowedRedirectUris(
        config: ClientConfigurationProperties,
        allowedRedirectUris: List<String>?,
        errors: MutableList<ConfigurationException>
    ): List<URI>? {
        val listErrors = mutableListOf<ConfigurationException>()

        val allowedRedirectUris = allowedRedirectUris?.mapIndexedNotNull { index, uri ->
            try {
                parser.getAbsoluteUriOrThrow(
                    uri, "$CLIENTS_KEY.${config.id}.allowed-redirect-uris[$index]"
                ) { it }
            } catch (e: ConfigurationException) {
                listErrors.add(e)
                null
            }
        }

        return if (listErrors.isEmpty()) {
            allowedRedirectUris
        } else {
            errors.addAll(listErrors)
            null
        }
    }

    private suspend fun getScopes(
        key: String,
        scopes: List<String>?,
        errors: MutableList<ConfigurationException>
    ): List<Scope>? {
        val scopeErrors = mutableListOf<ConfigurationException>()

        val verifiedScopes = scopes?.mapIndexedNotNull { index, scope ->
            try {
                val verifiedScope = scopeManager.find(scope)
                if (verifiedScope == null) {
                    val error = configExceptionOf(
                        "$key[${index}]", "config.client.claim.invalid",
                        "scope" to scope
                    )
                    scopeErrors.add(error)
                }
                verifiedScope
            } catch (t: Throwable) {
                // We do not had the error to the list since it is most likely already caused by another configuration error
                null
            }
        }

        return if (errors.isEmpty()) {
            verifiedScopes
        } else {
            errors.addAll(scopeErrors)
            null
        }
    }
}
