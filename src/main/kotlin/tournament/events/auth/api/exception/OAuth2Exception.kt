package tournament.events.auth.api.exception

import io.micronaut.http.HttpStatus
import tournament.events.auth.business.model.oauth2.OAuth2ErrorCode
import tournament.events.auth.exception.LocalizedHttpException

class OAuth2Exception(
    val errorCode: OAuth2ErrorCode,
    val detailsId: String,
    val descriptionId: String? = null,
    val values: Map<String, Any?> = emptyMap(),
) : Exception(formatMessage(errorCode, detailsId)) {

    val status: HttpStatus = errorCode.status

    companion object {
        private fun formatMessage(errorCode: OAuth2ErrorCode, detailsId: String?): String {
            return "OAuth2 - ${errorCode.errorCode} - $detailsId"
        }
    }
}

fun OAuth2Exception.toHttpException(httpStatus: HttpStatus) = LocalizedHttpException(
    status = httpStatus,
    detailsId = detailsId,
    descriptionId = descriptionId,
    values = values
)

fun oauth2ExceptionOf(
    errorCode: OAuth2ErrorCode,
    detailsId: String,
    vararg values: Pair<String, Any?>
) = OAuth2Exception(errorCode, detailsId, null, mapOf(*values))

fun oauth2ExceptionOf(
    errorCode: OAuth2ErrorCode,
    detailsId: String,
    descriptionId: String,
    vararg values: Pair<String, Any?>
) = OAuth2Exception(errorCode, detailsId, descriptionId, mapOf(*values))
