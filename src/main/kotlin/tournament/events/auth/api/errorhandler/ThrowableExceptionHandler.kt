package tournament.events.auth.api.errorhandler

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Inject
import tournament.events.auth.api.resource.error.ErrorResource
import tournament.events.auth.util.loggerForClass

class ThrowableExceptionHandler<T: Throwable>(
    @Inject private val exceptionConverter: ExceptionConverter,
    @Inject private val exceptionHandler: LocalizedHttpExceptionHandler
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
