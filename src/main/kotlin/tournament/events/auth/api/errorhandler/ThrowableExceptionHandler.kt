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
import tournament.events.auth.exception.LocalizedHttpException
import tournament.events.auth.exception.httpExceptionOf
import tournament.events.auth.util.loggerForClass
import java.util.*

@Produces
@Singleton
class ThrowableExceptionHandler(
    private val errorResourceMapper: ErrorResourceMapper
) : ExceptionHandler<Throwable, HttpResponse<ErrorResource>> {

    val logger = loggerForClass()

    override fun handle(request: HttpRequest<*>, exception: Throwable): HttpResponse<ErrorResource> {
        logger.error("Unexpected error occurred: ${exception.message}", exception)

        val locale = request.locale.orElse(Locale.US)

        val httpException = LocalizedHttpException(
            status = INTERNAL_SERVER_ERROR,
            detailsId = "internal_server_error",
            descriptionId = "description.internal_server_error",
            throwable = exception
        )
        return HttpResponseFactory.INSTANCE.status(
            httpException.status,
            errorResourceMapper.toResource(httpException, locale)
        )
    }
}
