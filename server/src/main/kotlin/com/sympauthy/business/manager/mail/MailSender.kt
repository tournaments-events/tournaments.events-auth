package com.sympauthy.business.manager.mail

import io.micronaut.email.EmailSender
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.*

/**
 * This class MUST be injected as an [Optional] instead of [EmailSender].
 * Trying to inject [EmailSender] as an optional will result into the application not starting when there is no
 * email provider configured.
 * It will fail to start with the error: ```JavaMail configuration does not contain any properties.```
 */
/*
FIXME
@Requirements(
    Requires(bean = MailPropertiesProvider::class),
    Requires(configuration = "javamail.properties")
)
 */
@Singleton
data class MailSender(
    @Inject val sender: EmailSender<Any, Any>
)
