package tournament.events.auth.api.error

import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpResponseFactory
import io.micronaut.http.HttpStatus.BAD_REQUEST
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import io.micronaut.validation.exceptions.ConstraintExceptionHandler
import jakarta.inject.Singleton
import tournament.events.auth.api.model.ErrorResource
import java.util.*
import javax.validation.ConstraintViolationException

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
