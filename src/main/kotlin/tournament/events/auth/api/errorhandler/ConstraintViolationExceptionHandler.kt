package tournament.events.auth.api.errorhandler

import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpResponseFactory
import io.micronaut.http.HttpStatus.BAD_REQUEST
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import io.micronaut.validation.exceptions.ConstraintExceptionHandler
import jakarta.inject.Inject
import jakarta.inject.Singleton
import jakarta.validation.ConstraintViolationException
import tournament.events.auth.api.mapper.ErrorResourceMapper
import tournament.events.auth.api.model.error.ErrorResource
import tournament.events.auth.server.ErrorMessages
import java.util.*

/* FIXME
@Produces
@Singleton
@Replaces(ConstraintExceptionHandler::class)
class ConstraintViolationExceptionHandler(
    private val errorResourceMapper: ErrorResourceMapper
) : ExceptionHandler<ConstraintViolationException, HttpResponse<ErrorResource>> {

    override fun handle(request: HttpRequest<*>, exception: ConstraintViolationException): HttpResponse<ErrorResource> {
        val locale = request.locale.orElse(Locale.US)
        return HttpResponseFactory.INSTANCE.status(
            BAD_REQUEST,
            errorResourceMapper.toErrorResource(exception, locale)
        )
    }
}
 */
