package com.sympauthy.config

import com.sympauthy.config.exception.ConfigurationException
import com.sympauthy.config.model.*
import com.sympauthy.exception.LocalizedException
import com.sympauthy.server.ErrorMessages
import com.sympauthy.util.loggerForClass
import io.micronaut.context.MessageSource
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.discovery.event.ServiceReadyEvent
import io.micronaut.health.HealthStatus.DOWN
import io.micronaut.health.HealthStatus.UP
import io.micronaut.management.health.indicator.HealthIndicator
import io.micronaut.management.health.indicator.HealthResult
import io.micronaut.management.health.indicator.annotation.Readiness
import io.micronaut.scheduling.annotation.Async
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.reactivestreams.Publisher
import reactor.core.publisher.Mono
import java.util.*

@Singleton
@Readiness
open class ConfigReadinessIndicator(
    @ErrorMessages @Inject private val messageSource: MessageSource,
    @Inject private val advancedConfig: AdvancedConfig,
    @Inject private val authConfig: AuthConfig,
    @Inject private val claimsConfig: ClaimsConfig,
    @Inject private val passwordAuthConfig: PasswordAuthConfig,
    @Inject private val urlsConfig: UrlsConfig
) : HealthIndicator, ApplicationEventListener<ServiceReadyEvent> {

    private val logger = loggerForClass()

    private val configs = listOf(
        advancedConfig,
        authConfig,
        claimsConfig,
        passwordAuthConfig,
        urlsConfig
    )

    override fun getResult(): Publisher<HealthResult> {
        val configurationErrors = configs.flatMap { it.configurationErrors ?: emptyList() }

        val builder = HealthResult.builder(HEALTH_INDICATOR_NAME)
        if (configurationErrors.isEmpty()) {
            builder.status(UP)
        } else {
            builder.status(DOWN)
            builder.details(configurationErrors.associate(::getKeyAndLocalizedMessage))
        }
        return Mono.just(builder.build())
    }

    @Async
    override fun onApplicationEvent(event: ServiceReadyEvent) {
        val configurationErrors = configs.flatMap { it.configurationErrors ?: emptyList() }
        if (configurationErrors.isEmpty()) {
            logger.info("No error detected in the configuration.")
        } else {
            logger.error("One or more errors detected in the configuration. This application will NOT OPERATE PROPERLY.")
            configurationErrors
                .map(::getKeyAndLocalizedMessage)
                .forEach { (key, localizedErrorMessage) ->
                    logger.error("- $key: $localizedErrorMessage")
                }
        }
    }

    private fun getKeyAndLocalizedMessage(error: Exception): Pair<String, String?> {
        return when (error) {
            is LocalizedException -> {
                val key = error.values["key"]?.toString() ?: ""
                val localizedErrorMessage = messageSource.getMessage(error.detailsId, Locale.US, error.values)
                    .orElse(error.detailsId)
                key to localizedErrorMessage
            }

            is ConfigurationException -> {
                val localizedErrorMessage = messageSource.getMessage(error.messageId, Locale.US, error.values)
                    .orElse(error.messageId)
                error.key to localizedErrorMessage
            }

            else -> error.javaClass.name to error.message
        }
    }

    companion object {
        private const val HEALTH_INDICATOR_NAME = "config"
    }
}
