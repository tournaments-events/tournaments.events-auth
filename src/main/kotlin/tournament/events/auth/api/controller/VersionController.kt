package tournament.events.auth.api.controller

import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS
import io.reactivex.rxjava3.core.Single
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import kotlinx.coroutines.rx3.await
import tournament.events.auth.OpenAPI
import tournament.events.auth.api.resource.VersionResource
import tournament.events.auth.exception.httpExceptionOf

@Controller("/version")
@Secured(IS_ANONYMOUS)
class VersionController {

    private val apiVersion = Single.create {
        val annotation = OpenAPI::class.annotations
            .filterIsInstance<OpenAPIDefinition>()
            .firstOrNull()
        if (annotation != null) {
            it.onSuccess(annotation.info.version)
        } else {
            it.onError(httpExceptionOf(HttpStatus.NOT_FOUND, "not_found"))
        }
    }.cache()

    @Get
    suspend fun get(): VersionResource {
        val apiVersion = apiVersion.await()
        return VersionResource(
            apiVersions = listOf(apiVersion)
        )
    }
}
