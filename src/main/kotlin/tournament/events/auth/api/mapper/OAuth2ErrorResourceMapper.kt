package tournament.events.auth.api.mapper

import io.micronaut.context.MessageSource
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.api.exception.OAuth2Exception
import tournament.events.auth.api.resource.error.OAuth2ErrorResource
import tournament.events.auth.server.ErrorMessages
import java.util.*

@Singleton
class OAuth2ErrorResourceMapper(
    @Inject @ErrorMessages private val messageSource: MessageSource
) {

    fun toResource(
        error: OAuth2Exception,
        locale: Locale
    ): OAuth2ErrorResource {
        return OAuth2ErrorResource(
            errorCode = error.errorCode.errorCode,
            details = error.detailsId.translate(error, locale),
            description = error.descriptionId.translate(error, locale)
        )
    }

    private fun String?.translate(error: OAuth2Exception, locale: Locale): String? {
        return this?.let { messageSource.getMessage(this, locale, error.values) }
            ?.orElse(null)
    }
}
