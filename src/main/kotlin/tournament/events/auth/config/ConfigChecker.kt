package tournament.events.auth.config

import io.micronaut.context.MessageSource
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.discovery.event.ServiceReadyEvent
import io.micronaut.scheduling.annotation.Async
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.config.exception.ConfigurationException
import tournament.events.auth.config.model.*
import tournament.events.auth.exception.LocalizedException
import tournament.events.auth.util.loggerForClass
import java.util.*

@Singleton
open class ConfigChecker(
    @Inject private val messageSource: MessageSource,
    @Inject private val advancedConfig: AdvancedConfig,
    @Inject private val authConfig: AuthConfig,
    @Inject private val claimsConfig: ClaimsConfig,
    @Inject private val passwordAuthConfig: PasswordAuthConfig,
    @Inject private val urlsConfig: UrlsConfig
) : ApplicationEventListener<ServiceReadyEvent> {

    private val logger = loggerForClass()

    private val configs = listOf(
        advancedConfig,
        authConfig,
        claimsConfig,
        passwordAuthConfig,
        urlsConfig
    )

    @Async
    override fun onApplicationEvent(event: ServiceReadyEvent) {
        val configurationErrors = configs.flatMap { it.configurationErrors ?: emptyList() }
        if (configurationErrors.isEmpty()) {
            logger.info("No error detected in the configuration.")
        } else {
            logger.error("One or more errors detected in the configuration. This application will NOT OPERATE PROPERLY.")
            configurationErrors.forEach(this::logConfigError)
        }
    }

    private fun logConfigError(error: Exception) {
        if (error is LocalizedException) {
            val localizedErrorMessage = messageSource.getMessage(error.detailsId, Locale.US, error.values)
                .orElse(error.detailsId)
            logger.error("- $localizedErrorMessage")
        } else if (error is ConfigurationException) {
            val values = mapOf("key" to error.key) + error.values
            val localizedErrorMessage = messageSource.getMessage(error.messageId, Locale.US, values)
                .orElse(error.messageId)
            logger.error("- $localizedErrorMessage")
        }
    }
}
