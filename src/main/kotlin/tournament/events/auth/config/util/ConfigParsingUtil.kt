package tournament.events.auth.config.util

import io.micronaut.http.HttpStatus
import io.micronaut.http.uri.UriBuilder
import tournament.events.auth.business.exception.businessExceptionOf
import java.net.URI

fun <C : Any, R : Any> getOrThrow(config: C, key: String, value: (C) -> R?): R {
    return value(config) ?: throw businessExceptionOf(
        HttpStatus.INTERNAL_SERVER_ERROR, "exception.config.missing", "key" to key
    )
}

fun <C : Any> getStringOrThrow(config: C, key: String, value: (C) -> String?): String {
    val value = getOrThrow(config, key, value)
    if (value.isBlank()) {
        throw businessExceptionOf(HttpStatus.INTERNAL_SERVER_ERROR, "exception.config.empty", "key" to key)
    }
    return value
}

fun <C : Any> getUriOrThrow(
    config: C,
    key: String,
    value: (C) -> String?
): URI {
    val uri = getOrThrow(config, key, value).let(UriBuilder::of).build()
    if (uri.scheme.isNullOrBlank() || uri.host.isNullOrBlank()) {
        throw businessExceptionOf(HttpStatus.INTERNAL_SERVER_ERROR, "exception.config.invalid_url", "key" to key)
    }
    return uri
}

inline fun <C : Any, reified T : Enum<T>> getEnumOrThrow(
    config: C,
    key: String,
    noinline value: (C) -> String?
): T {
    val serializedValue = getStringOrThrow(config, key, value)
    return convertToEnum(key, serializedValue)
}

inline fun <C : Any, reified T : Enum<T>> getEnum(
    config: C,
    key: String,
    defaultValue: T,
    value: (C) -> String?
): T {
    val serializedValue = value(config) ?: return defaultValue
    return convertToEnum(key, serializedValue)
}

inline fun <reified T : Enum<T>> convertToEnum(key: String, value: String): T {
    val valueMap = enumValues<T>()
        .map {
            val configName = it.name.lowercase().replace("_", "-")
            configName to it
        }
        .toMap()
    return valueMap[value] ?: throw businessExceptionOf(
        HttpStatus.INTERNAL_SERVER_ERROR, "exception.config.invalid_enum_value",
        "key" to key,
        "value" to value,
        "supportedValues" to valueMap.keys.joinToString(", ")
    )
}
