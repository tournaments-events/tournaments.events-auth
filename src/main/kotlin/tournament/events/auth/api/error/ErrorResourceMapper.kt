package tournament.events.auth.api.error

import io.micronaut.context.MessageSource
import io.micronaut.context.MessageSource.MessageContext
import io.micronaut.http.HttpStatus
import jakarta.inject.Singleton
import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import tournament.events.auth.api.model.AdditionalMessageResource
import tournament.events.auth.api.model.ErrorResource
import tournament.events.auth.util.AdditionalLocalizedMessage
import tournament.events.auth.util.LocalizedException
import tournament.events.auth.util.pathAsString
import java.util.*

@Singleton
class ErrorResourceMapper(
    private val messageSource: MessageSource
) {

    fun toErrorResource(
        exception: LocalizedException,
        locale: Locale
    ): ErrorResource {
        val localizedMessage = messageSource.getMessage(exception.messageResourceName, locale, exception.values)
        return ErrorResource(
            status = exception.status.code,
            code = exception.messageResourceName,
            message = localizedMessage.orElse(null),
            additionalMessages = exception.additionalMessages.mapNotNull { toAdditionalMessageResource(it, locale) }
        )
    }

    private fun toAdditionalMessageResource(
        message: AdditionalLocalizedMessage,
        locale: Locale
    ): AdditionalMessageResource? {
        val localizedMessage = messageSource.getMessage(message.messageResourceName, locale, message.values)
            .orElse(null) ?: return null
        return AdditionalMessageResource(
            path = message.path,
            code = message.messageResourceName,
            message = localizedMessage
        )
    }

    fun toErrorResource(
        exception: ConstraintViolationException,
        locale: Locale
    ): ErrorResource {
        val messageResourceName = "exception.validation.invalid"
        return ErrorResource(
            status = HttpStatus.BAD_REQUEST.code,
            code = messageResourceName,
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
}
