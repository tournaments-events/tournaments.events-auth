package tournament.events.auth.server

import io.micronaut.context.MessageSource
import io.micronaut.context.annotation.Factory
import io.micronaut.context.i18n.ResourceBundleMessageSource
import jakarta.inject.Qualifier
import jakarta.inject.Singleton
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
@MustBeDocumented
annotation class ErrorMessages

@Factory
class MessageSourceFactory {

    @Singleton
    @ErrorMessages
    fun provideMessageSource(): MessageSource {
        return ResourceBundleMessageSource("error_messages")
    }
}
