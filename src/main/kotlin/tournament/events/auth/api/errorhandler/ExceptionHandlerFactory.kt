package tournament.events.auth.api.errorhandler

import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.security.authentication.AuthorizationException
import io.micronaut.security.authentication.DefaultAuthorizationExceptionHandler
import jakarta.inject.Inject
import jakarta.inject.Singleton

/**
 * This class replace all defaults exception handler provided by frameworks (like Micronaut security)
 * by our own [ThrowableExceptionHandler].
 *
 * This helps to make our error messages uniform across the whole application
 * and to keep the logic factorized into the [ExceptionConverter] and the [ThrowableExceptionHandler].
 */
@Factory
class ExceptionHandlerFactory(
    @Inject private val exceptionConverter: ExceptionConverter,
    @Inject private val exceptionHandler: LocalizedHttpExceptionHandler
) {

    @Singleton
    @Replaces(DefaultAuthorizationExceptionHandler::class)
    fun authorizationExceptionHandler() = exceptionHandler<AuthorizationException>()

    @Singleton
    fun throwableExceptionHandler() = exceptionHandler<Throwable>()

    private inline fun <reified T : Throwable> exceptionHandler() =
        ThrowableExceptionHandler<T>(exceptionConverter, exceptionHandler)
}
