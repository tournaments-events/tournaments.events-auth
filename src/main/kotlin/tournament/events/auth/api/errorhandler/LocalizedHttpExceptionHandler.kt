package tournament.events.auth.api.errorhandler

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpResponseFactory
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Singleton
import tournament.events.auth.api.mapper.ErrorResourceMapper
import tournament.events.auth.api.resource.error.ErrorResource
import tournament.events.auth.exception.LocalizedHttpException
import tournament.events.auth.util.orDefault

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
