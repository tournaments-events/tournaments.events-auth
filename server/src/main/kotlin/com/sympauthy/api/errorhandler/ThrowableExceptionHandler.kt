package com.sympauthy.api.errorhandler

import com.sympauthy.api.resource.error.ErrorResource
import com.sympauthy.util.loggerForClass
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.server.exceptions.ExceptionHandler

class ThrowableExceptionHandler<T: Throwable>(
    private val exceptionConverter: ExceptionConverter,
    private val exceptionHandler: LocalizedHttpExceptionHandler
) : ExceptionHandler<T, HttpResponse<ErrorResource>> {

    val logger = loggerForClass()

    override fun handle(request: HttpRequest<*>, throwable: T): HttpResponse<ErrorResource> {
        val httpException = exceptionConverter.normalize(throwable)
        if (httpException.detailsId == "internal_server_error") {
            logger.error("Unexpected error occurred: ${throwable.message}", throwable)
        }
        return exceptionHandler.handle(request, httpException)
    }
}
