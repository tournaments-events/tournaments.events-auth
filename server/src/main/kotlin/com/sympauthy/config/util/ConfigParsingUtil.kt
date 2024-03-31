package com.sympauthy.config.util

import com.sympauthy.exception.localizedExceptionOf
import com.sympauthy.util.toAbsoluteUri
import java.net.URI

fun <C : Any, R : Any> getOrThrow(config: C, key: String, value: (C) -> R?): R {
    return value(config) ?: throw localizedExceptionOf(
        "config.missing", "key" to key
    )
}

fun <C : Any> getStringOrThrow(config: C, key: String, value: (C) -> String?): String {
    val value = getOrThrow(config, key, value)
    if (value.isBlank()) {
        throw localizedExceptionOf("config.empty", "key" to key)
    }
    return value
}

fun <C : Any> getUriOrThrow(
    config: C,
    key: String,
    value: (C) -> String?
): URI {
    return getOrThrow(config, key, value).toAbsoluteUri() ?: throw localizedExceptionOf(
        "config.invalid_url",
        "key" to key
    )
}

inline fun <reified T : Enum<T>> convertToEnum(key: String, value: String): T {
    val valueMap = enumValues<T>()
        .map {
            val configName = it.name.lowercase().replace("_", "-")
            configName to it
        }
        .toMap()
    return valueMap[value] ?: throw localizedExceptionOf(
        "config.invalid_enum_value",
        "key" to key,
        "value" to value,
        "supportedValues" to valueMap.keys.joinToString(", ")
    )
}
