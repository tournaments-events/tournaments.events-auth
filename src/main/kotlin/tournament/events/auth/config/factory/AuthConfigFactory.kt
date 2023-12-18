package tournament.events.auth.config.factory

import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import tournament.events.auth.config.model.AuthConfig
import tournament.events.auth.config.model.EnabledAuthConfig
import tournament.events.auth.config.properties.AuthConfigurationProperties

@Factory
class AuthConfigFactory {

    @Singleton
    fun provideAuthConfig(
        properties: AuthConfigurationProperties
    ): AuthConfig {
        return EnabledAuthConfig(
            issuer = properties.issuer,
            audience = properties.audience
        )
    }
}
