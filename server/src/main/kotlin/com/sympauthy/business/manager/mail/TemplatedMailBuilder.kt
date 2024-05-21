package com.sympauthy.business.manager.mail

import io.micronaut.context.MessageSource
import io.micronaut.email.BodyType
import io.micronaut.email.Email
import io.micronaut.email.MultipartBody
import io.micronaut.email.StringBody
import io.micronaut.email.template.TemplateBody
import io.micronaut.views.ModelAndView
import java.util.*

class TemplatedMailBuilder(
    htmlTemplate: String? = null,
    textTemplate: String? = null,
    private val locale: Locale,
    private val messageSource: MessageSource,
) {
    val builder: Email.Builder
    private val model = mutableMapOf<String, Any>()

    init {
        val htmlBody = htmlTemplate
            ?.let { ModelAndView(it, model) }
            ?.let { TemplateBody(BodyType.HTML, it) }
        val textBody = textTemplate
            ?.let { ModelAndView(it, model) }
            ?.let { TemplateBody(BodyType.TEXT, it) }
        val body = when {
            htmlBody != null && textBody != null -> MultipartBody(htmlBody, textBody)
            htmlBody != null -> htmlBody
            textBody != null -> textBody
            else -> StringBody("")
        }

        builder = Email.builder()
            .body(body)
    }

    fun to(email: String) = this.apply {
        builder.to(email)
    }

    /**
     * Set the key of title of the mail in the resource bundle.
     * The title is often shown in email notifications, like on Android devices.
     */
    fun localizedTitle(titleKey: String) = this.apply { setLocalizedText(TITLE_MODEL_KEY, titleKey) }

    /**
     * Set the key of the preview text in the resource bundle.
     */
    fun localizedPreviewText(previewTextKey: String) = this.apply {
        setLocalizedText(PREVIEW_TEXT_MODEL_KEY, previewTextKey)
    }

    fun set(key: String, value: Any) = this.apply { this.model[key] = value }

    fun setLocalizedText(key: String, messageKey: String) = this.apply {
        val localizedMessage = messageSource.getMessage(messageKey, locale).orElse(null)
        set(key, localizedMessage)
    }

    companion object {
        const val TITLE_MODEL_KEY = "title"
        const val PREVIEW_TEXT_MODEL_KEY = "previewText"
    }
}
