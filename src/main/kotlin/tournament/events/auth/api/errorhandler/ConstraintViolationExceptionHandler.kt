package tournament.events.auth.api.errorhandler

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
