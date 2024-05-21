package com.sympauthy.business.manager.mail

import com.sympauthy.server.MailMessages
import io.micronaut.context.MessageSource
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.*

@Singleton
class TemplatedMailBuilderFactory(
    @Inject @MailMessages private val messageSource: MessageSource
) {

    /**
     *
     */
    fun builder(
        template: String,
        locale: Locale
    ): TemplatedMailBuilder {
        return TemplatedMailBuilder(
            htmlTemplate = template,
            messageSource = messageSource,
            locale = locale,
        )
    }
}
