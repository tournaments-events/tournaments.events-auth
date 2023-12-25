package tournament.events.auth.api.errorhandler

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpResponseFactory
import io.micronaut.http.HttpStatus
import io.micronaut.http.HttpStatus.INTERNAL_SERVER_ERROR
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Singleton
import tournament.events.auth.api.mapper.ErrorResourceMapper
import tournament.events.auth.api.model.error.ErrorResource
import tournament.events.auth.exception.LocalizedException
import java.util.*

@Produces
@Singleton
class LocalizedExceptionHandler(
    private val errorResourceMapper: ErrorResourceMapper
) : ExceptionHandler<LocalizedException, HttpResponse<ErrorResource>> {

    override fun handle(request: HttpRequest<*>, exception: LocalizedException): HttpResponse<ErrorResource> {
        val locale = request.locale.orElse(Locale.US)
        return HttpResponseFactory.INSTANCE.status(
            INTERNAL_SERVER_ERROR,
            errorResourceMapper.toResource(INTERNAL_SERVER_ERROR, exception, locale)
        )
    }
}
