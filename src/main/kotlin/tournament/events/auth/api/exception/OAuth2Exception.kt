package tournament.events.auth.api.exception

import io.micronaut.http.HttpStatus
import tournament.events.auth.business.model.auth.oauth2.OAuth2ErrorCode

class OAuth2Exception(
    val errorCode: OAuth2ErrorCode,
    val detailsId: String? = null,
    val descriptionId: String? = null,
    val values: Map<String, Any?> = emptyMap(),
) : Exception(formatMessage(errorCode, detailsId)) {

    val status: HttpStatus = errorCode.status

    companion object {
        private fun formatMessage(errorCode: OAuth2ErrorCode, detailsId: String?): String {
            return when {
                detailsId != null -> "OAuth2 - ${errorCode.errorCode} - $detailsId"
                else -> "OAuth2 - ${errorCode.errorCode}"
            }
        }
    }
}

fun oauth2ExceptionOf(
    errorCode: OAuth2ErrorCode
) = OAuth2Exception(errorCode)

fun oauth2ExceptionOf(
    errorCode: OAuth2ErrorCode,
    detailsId: String,
    vararg values: Pair<String, Any?>
) = OAuth2Exception(errorCode, null, detailsId, mapOf(*values))

fun oauth2ExceptionOf(
    errorCode: OAuth2ErrorCode,
    detailsId: String,
    descriptionId: String,
    vararg values: Pair<String, Any?>
) = OAuth2Exception(errorCode, descriptionId, detailsId, mapOf(*values))
