package tournament.events.auth.business.manager.provider

import com.jayway.jsonpath.JsonPath
import io.micronaut.context.MessageSource
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.discovery.event.ServiceReadyEvent
import io.micronaut.http.HttpStatus
import io.micronaut.http.HttpStatus.INTERNAL_SERVER_ERROR
import io.micronaut.scheduling.annotation.Async
import io.reactivex.rxjava3.core.Single
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.rx3.await
import tournament.events.auth.business.exception.BusinessException
import tournament.events.auth.business.exception.businessExceptionOf
import tournament.events.auth.business.model.provider.DisabledProvider
import tournament.events.auth.business.model.provider.EnabledProvider
import tournament.events.auth.business.model.provider.Provider
import tournament.events.auth.business.model.provider.ProviderUserInfoPathKey
import tournament.events.auth.business.model.provider.ProviderUserInfoPathKey.EMAIL
import tournament.events.auth.business.model.provider.ProviderUserInfoPathKey.SUB
import tournament.events.auth.business.model.provider.config.ProviderAuthConfig
import tournament.events.auth.business.model.provider.config.ProviderOauth2Config
import tournament.events.auth.business.model.provider.config.ProviderUserInfoConfig
import tournament.events.auth.business.model.user.UserMergingStrategy.BY_MAIL
import tournament.events.auth.config.model.AdvancedConfig
import tournament.events.auth.config.model.orThrow
import tournament.events.auth.config.properties.ProviderConfigurationProperties
import tournament.events.auth.config.properties.ProviderConfigurationProperties.Companion.PROVIDERS_CONFIG_KEY
import tournament.events.auth.config.util.convertToEnum
import tournament.events.auth.config.util.getStringOrThrow
import tournament.events.auth.config.util.getUriOrThrow
import tournament.events.auth.util.loggerForClass
import java.util.*

/**
 * Manager in charge of verifying the providers available in the configuration and transforming them
 * in a more usable form for the rest of the business logic.
 */
@Singleton
open class ProviderConfigManager(
    @Inject private val providers: List<ProviderConfigurationProperties>,
    @Inject private val messageSource: MessageSource,
    @Inject private val advancedConfig: AdvancedConfig
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
            .firstOrNull() ?: throw businessExceptionOf(HttpStatus.BAD_REQUEST, "provider.missing")
    }

    private fun configureProviders(): List<Provider> {
        logger.info("Detected ${providers.count()} provider(s) in the configuration.")
        val configuredProviders = providers.map {
            try {
                configureProvider(it)
            } catch (e: BusinessException) {
                val localizedErrorMessage = messageSource.getMessage(e.detailsId, Locale.US, e.values)
                    .orElse(e.detailsId)
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

    internal fun configureProvider(config: ProviderConfigurationProperties): EnabledProvider {
        return EnabledProvider(
            id = config.id,
            name = getStringOrThrow(config, "$PROVIDERS_CONFIG_KEY.name", ProviderConfigurationProperties::name),
            userInfo = configureProviderUserInfo(config),
            auth = configureProviderAuth(config)
        )
    }

    private fun configureProviderUserInfo(config: ProviderConfigurationProperties): ProviderUserInfoConfig {
        val userInfo = config.userInfo ?: throw businessExceptionOf(
            INTERNAL_SERVER_ERROR, "config.provider.user_info.missing"
        )

        return ProviderUserInfoConfig(
            uri = getUriOrThrow(
                userInfo,
                "${PROVIDERS_CONFIG_KEY}.user-info.url",
                ProviderConfigurationProperties.UserInfoConfig::url
            ),
            paths = configureProviderUserInfoPaths(userInfo)
        )
    }

    private fun configureProviderUserInfoPaths(
        userInfo: ProviderConfigurationProperties.UserInfoConfig
    ): Map<ProviderUserInfoPathKey, JsonPath> {
        val userInfoPathsKey = "$PROVIDERS_CONFIG_KEY.user-info.paths"
        val userInfoPaths = userInfo.paths ?: throw businessExceptionOf(
            INTERNAL_SERVER_ERROR, "config.missing",
            "key" to userInfoPathsKey
        )
        val paths = userInfoPaths
            .map { (key, value) ->
                val pathKey = convertToEnum<ProviderUserInfoPathKey>(
                    "$userInfoPathsKey.$key", key
                )
                val rawPath = value ?: throw businessExceptionOf(
                    INTERNAL_SERVER_ERROR, "config.provider.user_info.invalid_value",
                    "key" to "$userInfoPathsKey.$key"
                )
                val path = try {
                    JsonPath.compile(rawPath)
                } catch (e: Throwable) {
                    throw BusinessException(
                        status = INTERNAL_SERVER_ERROR,
                        detailsId = "config.provider.user_info.invalid_value",
                        values = mapOf(
                            "key" to "$userInfoPathsKey.$key"
                        ),
                        throwable = e
                    )
                }
                pathKey to path
            }
            .toMap()
        if (paths[SUB] == null) {
            throw businessExceptionOf(
                INTERNAL_SERVER_ERROR, "config.provider.user_info.missing_subject_key",
                "key" to "${PROVIDERS_CONFIG_KEY}.user-info.paths"
            )
        }
        if (advancedConfig.orThrow().userMergingStrategy == BY_MAIL && paths[EMAIL] == null) {
            throw businessExceptionOf(
                INTERNAL_SERVER_ERROR, "config.provider.user_info.missing_email_key",
                "key" to "${PROVIDERS_CONFIG_KEY}.user-info.paths"
            )
        }
        return paths
    }

    private fun configureProviderAuth(config: ProviderConfigurationProperties): ProviderAuthConfig {
        return when {
            config.oauth2 != null -> configureProviderOauth2(config, config.oauth2!!)
            else -> throw businessExceptionOf(
                INTERNAL_SERVER_ERROR, "config.auth.missing"
            )
        }
    }

    private fun configureProviderOauth2(
        config: ProviderConfigurationProperties,
        oauth2: ProviderConfigurationProperties.Oauth2Config
    ): ProviderOauth2Config {
        return ProviderOauth2Config(
            clientId = getStringOrThrow(
                oauth2,
                "${PROVIDERS_CONFIG_KEY}.${config.id}.client-id",
                ProviderConfigurationProperties.Oauth2Config::clientId
            ),
            clientSecret = getStringOrThrow(
                oauth2,
                "${PROVIDERS_CONFIG_KEY}.${config.id}.client-secret",
                ProviderConfigurationProperties.Oauth2Config::clientSecret
            ),
            scopes = oauth2.scopes,
            authorizationUri = getUriOrThrow(
                oauth2,
                "${PROVIDERS_CONFIG_KEY}.${config.id}.authorization-url",
                ProviderConfigurationProperties.Oauth2Config::authorizationUrl
            ),
            tokenUri = getUriOrThrow(
                oauth2,
                "${PROVIDERS_CONFIG_KEY}.${config.id}.token-url",
                ProviderConfigurationProperties.Oauth2Config::tokenUrl
            )
        )
    }
}
