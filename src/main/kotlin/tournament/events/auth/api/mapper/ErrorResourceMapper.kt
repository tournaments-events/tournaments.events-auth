package tournament.events.auth.api.mapper

import io.micronaut.context.MessageSource
import io.micronaut.http.HttpStatus
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.api.model.error.ErrorResource
import tournament.events.auth.exception.LocalizedException
import tournament.events.auth.exception.LocalizedHttpException
import tournament.events.auth.server.ErrorMessages
import java.util.*

@Singleton
class ErrorResourceMapper(
    @Inject @ErrorMessages private val messageSource: MessageSource
) {

    fun toResource(
        exception: LocalizedHttpException,
        locale: Locale
    ) = toResource(exception.status, exception, locale)

    fun toResource(
        status: HttpStatus,
        exception: LocalizedException,
        locale: Locale
    ): ErrorResource {
        val descriptionId = when {
            exception.descriptionId != null -> exception.descriptionId
            status.code in 500 until 600 -> "description.server_error"
            else -> null
        }

        return ErrorResource(
            status = status.code,
            errorCode = exception.detailsId,
            description = descriptionId?.let { messageSource.getMessage(it, locale, exception.values) }?.orElse(null),
            details = messageSource.getMessage(exception.detailsId, locale, exception.values).orElse(null)
        )
    }

    /*
    fun toErrorResource(
        exception: ConstraintViolationException,
        locale: Locale
    ): ErrorResource {
        val messageResourceName = "exception.validation.invalid"
        return ErrorResource(
            status = HttpStatus.BAD_REQUEST.code,
            errorCode = messageResourceName,
            message = messageSource.getMessage(messageResourceName, locale).orElse(null),
            additionalMessages = exception.constraintViolations
                .map { toAdditionalMessageResource(it, locale) }
                .ifEmpty { null }
        )
    }

    private fun toAdditionalMessageResource(
        constraintViolation: ConstraintViolation<*>,
        locale: Locale
    ): AdditionalMessageResource {
        val context = MessageContext.of(locale)
        return AdditionalMessageResource(
            path = constraintViolation.pathAsString,
            message = messageSource.interpolate(constraintViolation.messageTemplate, context)
        )
    }
     */
}
