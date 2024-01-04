package tournament.events.auth.config.model

import io.micronaut.http.uri.UriBuilder
import tournament.events.auth.config.exception.ConfigurationException
import java.net.URI

sealed class UrlsConfig(
    configurationErrors: List<ConfigurationException>? = null
) : Config(configurationErrors)

class EnabledUrlsConfig(
    val root: URI,
    val signIn: URI
): UrlsConfig()

class DisabledUrlsConfig(
    configurationErrors: List<ConfigurationException>
) : UrlsConfig(configurationErrors)

fun UrlsConfig.orThrow(): EnabledUrlsConfig {
    return when (this) {
        is EnabledUrlsConfig -> this
        is DisabledUrlsConfig -> throw this.invalidConfig
    }
}

fun UrlsConfig.buildUponRoot(): UriBuilder {
    return UriBuilder.of(orThrow().root)
}

val UrlsConfig.authorizeUri: URI
    get() = buildUponRoot()
        .path("/api/oauth2/authorize")
        .build()

val UrlsConfig.tokenUri: URI
    get() = buildUponRoot()
        .path("/api/oauth2/token")
        .build()

val UrlsConfig.userInfoUri: URI
    get() = buildUponRoot()
        .path("/api/openid/userinfo")
        .build()

val UrlsConfig.jwtUri: URI
    get() = buildUponRoot()
        .path(".well-known/public.jwks")
        .build()
