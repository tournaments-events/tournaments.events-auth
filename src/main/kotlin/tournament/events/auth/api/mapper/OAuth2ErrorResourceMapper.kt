package tournament.events.auth.api.mapper

import io.micronaut.context.MessageSource
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.api.exception.OAuth2Exception
import tournament.events.auth.api.model.error.OAuth2ErrorResource
import tournament.events.auth.business.exception.BusinessException
import tournament.events.auth.business.model.oauth2.OAuth2ErrorCode
import tournament.events.auth.server.ErrorMessages

@Singleton
class OAuth2ErrorResourceMapper(
    @Inject @ErrorMessages private val messageSource: MessageSource
) {

    fun toResource(error: OAuth2Exception): OAuth2ErrorResource {
        return OAuth2ErrorResource(
            errorCode = error.errorCode.errorCode
        )
    }

    fun toResource(error: BusinessException): OAuth2ErrorResource {
        return OAuth2ErrorResource(
            errorCode = OAuth2ErrorCode.SERVER_ERROR.errorCode
        )
    }
}
