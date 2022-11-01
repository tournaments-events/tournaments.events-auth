package tournament.events.auth.api.error

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpResponseFactory
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Singleton
import tournament.events.auth.api.model.ErrorResource
import tournament.events.auth.business.exception.BusinessException
import java.util.*

@Produces
@Singleton
class BusinessExceptionHandler(
    private val errorResourceMapper: ErrorResourceMapper
) : ExceptionHandler<BusinessException, HttpResponse<ErrorResource>> {

    override fun handle(request: HttpRequest<*>, exception: BusinessException): HttpResponse<ErrorResource> {
        val locale = request.locale.orElse(Locale.US)
        return HttpResponseFactory.INSTANCE.status(
            exception.status, errorResourceMapper.toErrorResource(exception, locale)
        )
    }
}
