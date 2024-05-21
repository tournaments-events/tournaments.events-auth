package com.sympauthy.business.manager.validationcode

import com.sympauthy.business.manager.mail.MailSender
import com.sympauthy.business.manager.mail.TemplatedMailBuilderFactory
import com.sympauthy.business.model.code.ValidationCode
import com.sympauthy.business.model.code.ValidationCodeMedia.EMAIL
import com.sympauthy.business.model.user.User
import com.sympauthy.config.model.FeaturesConfig
import com.sympauthy.config.model.orThrow
import io.micronaut.context.annotation.Requires
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.*

/**
 * Components in charge of sending the validation code to the user by email.
 */
@Singleton
@Requires(classes = [MailSender::class])
class ValidationCodeMailSender(
    @Inject private val mailBuilderFactory: TemplatedMailBuilderFactory,
    @Inject private val mailSender: MailSender,
    @Inject private val uncheckedFeaturesConfig: FeaturesConfig
) : ValidationCodeMediaSender {

    override val media = EMAIL

    override val enabled: Boolean
        get() {
            return uncheckedFeaturesConfig.orThrow().emailValidation
        }

    override suspend fun sendValidationCode(
        user: User,
        validationCode: ValidationCode
    ) {
        val builder = mailBuilderFactory
            .builder(
                template = "mails/validation_code",
                locale = Locale.US // FIXME
            ).apply {
                set("code", validationCode.code)
                localizedTitle("mail.validation_code.title")
                localizedPreviewText("mail.validation_code.preview_text")
            }

        // FIXME Launch in IO thread to avoid blocking
        mailSender.sender.send(builder.builder)
    }
}
