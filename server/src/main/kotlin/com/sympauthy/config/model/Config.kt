package com.sympauthy.config.model

import com.sympauthy.exception.LocalizedException
import com.sympauthy.exception.localizedExceptionOf

/**
 * Base class of all configuration model that will be injected in the app.
 *
 * Each <Config> model will be a sealed class with two subclasses:
 * - Enabled<Config>Config: The class exposing the properties parsed, transformed and validated.
 * - Disabled<Config>Config: The class exposing the errors we encountered when we parsed, transformed and validated the
 * config properties.
 *
 * Everywhere in the app, the configuration models will be injected as <Config>.
 *
 * Each <Config> will expose an extension method allowing them to be downcast into their Enabled<Config>Config form.
 * This will be used across the app to obtain the properties or throw an error if the configuration is invalid.
 * ```kotlin
 * fun AdvancedConfig.orThrow(): EnabledAdvancedConfig {
 *     return when (this) {
 *         is EnabledAdvancedConfig -> this
 *         is DisabledAdvancedConfig -> throw this.invalidConfig
 *     }
 * }
 * ```
 */
open class Config(
    /**
     * List of errors that we detected in the configuration when parsing it and validating it.
     */
    val configurationErrors: List<Exception>? = null
)

val Config.invalidConfig: LocalizedException
    get() = localizedExceptionOf("config.invalid")
