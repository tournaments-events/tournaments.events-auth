package com.sympauthy.util

import io.micronaut.http.uri.UriBuilder
import java.net.URI

fun String?.toAbsoluteUri(
    authorizedSchemes: List<String> = listOf("http", "https")
): URI? {
    val uri = this?.let(UriBuilder::of)?.build() ?: return null
    return when {
        !authorizedSchemes.contains(uri.scheme) -> null
        uri.host.isNullOrBlank() -> null
        else -> uri
    }
}
