package tournament.events.auth.business.manager.auth

import io.micronaut.context.MessageSource
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.discovery.event.ServiceReadyEvent
import io.micronaut.http.HttpStatus
import io.micronaut.http.HttpStatus.INTERNAL_SERVER_ERROR
import io.micronaut.http.uri.UriBuilder
import io.micronaut.scheduling.annotation.Async
import io.reactivex.rxjava3.core.Single
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.rx3.await
import tournament.events.auth.business.exception.BusinessException
import tournament.events.auth.business.exception.businessExceptionOf
import tournament.events.auth.business.model.provider.*
import tournament.events.auth.business.model.provider.config.ProviderAuth
import tournament.events.auth.business.model.provider.config.ProviderOauth2
import tournament.events.auth.config.model.ProviderConfig
import tournament.events.auth.config.model.ProviderConfig.Companion.PROVIDERS_CONFIG_KEY
import tournament.events.auth.util.loggerForClass
import java.net.URI
import java.util.*

/**
 * Manager in charge of verifying the providers available in the configuration and transforming them
 * in a more usable form for the rest of the business logic.
 */
@Singleton
open class ProviderConfigManager(
    @Inject private val providers: List<ProviderConfig>,
    @Inject private val messageSource: MessageSource
) : ApplicationEventListener<ServiceReadyEvent> {

    private val logger = loggerForClass()

    private val configuredProviders = Single
        .create {
            it.onSuccess(configureProviders())
        }
        .cache()

    @Async
    override fun onApplicationEvent(event: ServiceReadyEvent) {
        configuredProviders.subscribe()
    }

    suspend fun listProviders(): List<Provider> {
        return configuredProviders.await()
    }

    suspend fun listEnabledProviders(): List<EnabledProvider> {
        return configuredProviders.await()
            .filterIsInstance<EnabledProvider>()
    }

    suspend fun findEnabledProviderById(id: String): EnabledProvider {
        return listEnabledProviders()
            .filter { it.id == id }
            .firstOrNull() ?: throw businessExceptionOf(HttpStatus.BAD_REQUEST, "exception.provider.missing")
    }

    private fun configureProviders(): List<Provider> {
        logger.info("Detected ${providers.count()} provider(s) in the configuration.")
        val configuredProviders = providers.map {
            try {
                configureProvider(it)
            } catch (e: BusinessException) {
                val localizedErrorMessage = messageSource.getMessage(e.messageResourceName, Locale.US, e.values)
                    .orElse(e.messageResourceName)
                logger.error("Failed to configure ${it.id}: ${localizedErrorMessage}")
                DisabledProvider(it.id, e)
            }
        }
        val enabledProviderCount = configuredProviders.filterIsInstance<EnabledProvider>().count()
        if (enabledProviderCount == providers.count()) {
            logger.info("All $enabledProviderCount provider(s) configured.")
        } else {
            logger.error("$enabledProviderCount/${providers.count()} provider(s) configured. Fix error(s) above.")
        }
        return configuredProviders
    }

    internal fun configureProvider(config: ProviderConfig): EnabledProvider {
        return EnabledProvider(
            id = config.id,
            name = getStringOrThrow(config, "$PROVIDERS_CONFIG_KEY.name", ProviderConfig::name),
            auth = configureProviderAuth(config)
        )
    }

    internal fun <C : Any, R : Any> getOrThrow(config: C, key: String, value: (C) -> R?): R {
        return value(config) ?: throw businessExceptionOf(
            INTERNAL_SERVER_ERROR, "exception.config.missing", "key" to key
        )
    }

    internal fun <C : Any> getStringOrThrow(config: C, key: String, value: (C) -> String?): String {
        val value = getOrThrow(config, key, value)
        if (value.isBlank()) {
            throw businessExceptionOf(INTERNAL_SERVER_ERROR, "exception.config.empty", "key" to key)
        }
        return value
    }

    private fun <C : Any> getUriOrThrow(config: C, key: String, value: (C) -> String?): URI {
        val uri = getOrThrow(config, key, value).let(UriBuilder::of).build()
        if (uri.scheme.isNullOrBlank() || uri.host.isNullOrBlank()) {
            throw businessExceptionOf(INTERNAL_SERVER_ERROR, "exception.config.invalid_url", "key" to key)
        }
        return uri
    }

    private fun configureProviderAuth(config: ProviderConfig): ProviderAuth {
        return when {
            config.oauth2 != null -> configureProviderOauth2(config, config.oauth2!!)
            else -> throw businessExceptionOf(
                INTERNAL_SERVER_ERROR, "exception.config.missing_auth"
            )
        }
    }

    private fun configureProviderOauth2(
        config: ProviderConfig,
        oauth2: ProviderConfig.Oauth2Config
    ): ProviderOauth2 {
        return ProviderOauth2(
            clientId = getStringOrThrow(
                oauth2,
                "${PROVIDERS_CONFIG_KEY}.${config.id}.client-id",
                ProviderConfig.Oauth2Config::clientId
            ),
            clientSecret = getStringOrThrow(
                oauth2,
                "${PROVIDERS_CONFIG_KEY}.${config.id}.client-secret",
                ProviderConfig.Oauth2Config::clientSecret
            ),
            scopes = oauth2.scopes,
            authorizationUri = getUriOrThrow(
                oauth2,
                "${PROVIDERS_CONFIG_KEY}.${config.id}.authorization-url",
                ProviderConfig.Oauth2Config::authorizationUrl
            ),
            tokenUri = getUriOrThrow(
                oauth2,
                "${PROVIDERS_CONFIG_KEY}.${config.id}.token-url",
                ProviderConfig.Oauth2Config::tokenUrl
            )
        )
    }
}
