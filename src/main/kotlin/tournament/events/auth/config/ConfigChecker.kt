package tournament.events.auth.config

import io.micronaut.context.MessageSource
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.discovery.event.ServiceReadyEvent
import io.micronaut.scheduling.annotation.Async
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.business.exception.BusinessException
import tournament.events.auth.config.model.AdvancedConfig
import tournament.events.auth.config.model.AuthConfig
import tournament.events.auth.util.loggerForClass
import java.util.*

@Singleton
open class ConfigChecker(
    @Inject private val messageSource: MessageSource,
    @Inject private val advancedConfig: AdvancedConfig,
    @Inject private val authConfig: AuthConfig
) : ApplicationEventListener<ServiceReadyEvent> {

    private val logger = loggerForClass()

    private val configs = listOf(
        advancedConfig,
        authConfig
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

    private fun logConfigError(error: BusinessException) {
        val localizedErrorMessage = messageSource.getMessage(error.messageResourceName, Locale.US, error.values)
            .orElse(error.messageResourceName)
        logger.error("- $localizedErrorMessage")
    }
}
