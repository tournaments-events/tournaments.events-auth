package tournament.events.auth.api.errorhandler

import io.micronaut.http.HttpStatus.*
import io.micronaut.security.authentication.AuthorizationException
import jakarta.inject.Singleton
import tournament.events.auth.api.exception.OAuth2Exception
import tournament.events.auth.api.exception.toHttpException
import tournament.events.auth.exception.LocalizedException
import tournament.events.auth.exception.LocalizedHttpException
import tournament.events.auth.exception.httpExceptionOf
import tournament.events.auth.exception.toHttpException

@Singleton
class ExceptionConverter {

    /**
     * Normalize all kinds of [throwable] that can thrown by various framework like Micronaut Security
     * into our common [LocalizedHttpException] with proper http status to respond and details/description
     * for the end-user.
     *
     * Currently unhandled exception will be converted into 500 error.
     */
    fun normalize(throwable: Throwable): LocalizedHttpException {
        return when (throwable) {
            is AuthorizationException -> toException(throwable)
            is LocalizedHttpException -> throwable
            is LocalizedException -> throwable.toHttpException(INTERNAL_SERVER_ERROR)
            is OAuth2Exception -> throwable.toHttpException()
            else -> httpExceptionOf(
                status = INTERNAL_SERVER_ERROR,
                detailsId = "internal_server_error",
                descriptionId = "description.internal_server_error",
                throwable = throwable
            )
        }
    }

    private fun toException(exception: AuthorizationException): LocalizedHttpException {
        return if (exception.authentication != null && exception.isForbidden) {
            httpExceptionOf(FORBIDDEN, "forbidden")
        } else {
            httpExceptionOf(
                status = UNAUTHORIZED,
                detailsId = "unauthorized",
                descriptionId = "description.unauthorized"
            )
        }
    }
}

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
