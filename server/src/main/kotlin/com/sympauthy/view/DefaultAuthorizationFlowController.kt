package com.sympauthy.view

import com.sympauthy.config.model.UrlsConfig
import com.sympauthy.config.model.getOrNull
import com.sympauthy.config.model.getUri
import com.sympauthy.util.loggerForClass
import com.sympauthy.view.DefaultAuthorizationFlowController.Companion.USER_FLOW_ENDPOINT
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.context.event.StartupEvent
import io.micronaut.core.io.ResourceResolver
import io.micronaut.http.MediaType.TEXT_HTML
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.server.types.files.StreamedFile
import io.swagger.v3.oas.annotations.Hidden
import jakarta.inject.Inject
import kotlin.jvm.optionals.getOrNull


/**
 * Serve the index.html of sympauthy-flow that have been added in the resources by the CI.
 */
@Hidden
@Controller(USER_FLOW_ENDPOINT)
class DefaultAuthorizationFlowController(
    @Inject private val resourceResolver: ResourceResolver,
    @Inject private val uncheckedUrlsConfig: UrlsConfig,
) : ApplicationEventListener<StartupEvent> {

    private val log = loggerForClass()

    @Get(value = "/{path:[^\\.]*}", produces = [TEXT_HTML])
    fun forward(path: String?): StreamedFile? {
        return resourceResolver.getResource("classpath:sympauthy-flow/index.html")
            ?.map(::StreamedFile)
            ?.getOrNull()
    }

    override fun onApplicationEvent(event: StartupEvent) {
        val urlsConfig = uncheckedUrlsConfig.getOrNull() ?: return
        log.info("Default end-user flow is enabled and available at: ${urlsConfig.getUri(USER_FLOW_ENDPOINT)}")
    }

    companion object {
        const val USER_FLOW_ENDPOINT = "/flow"
    }
}
