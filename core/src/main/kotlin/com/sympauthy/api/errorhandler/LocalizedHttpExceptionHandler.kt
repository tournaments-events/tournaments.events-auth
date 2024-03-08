package com.sympauthy.api.errorhandler

import com.sympauthy.api.mapper.ErrorResourceMapper
import com.sympauthy.api.resource.error.ErrorResource
import com.sympauthy.exception.LocalizedHttpException
import com.sympauthy.util.orDefault
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpResponseFactory
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Singleton

@Singleton
class LocalizedHttpExceptionHandler(
    private val errorResourceMapper: ErrorResourceMapper
) : ExceptionHandler<LocalizedHttpException, HttpResponse<ErrorResource>> {

    override fun handle(request: HttpRequest<*>, exception: LocalizedHttpException): HttpResponse<ErrorResource> {
        val locale = request.locale.orDefault()
        return HttpResponseFactory.INSTANCE.status(
            exception.status,
            errorResourceMapper.toResource(exception, locale)
        )
    }
}
