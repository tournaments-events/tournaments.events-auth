package com.sympauthy.business.model.code

import com.sympauthy.business.model.code.ValidationCodeMedia.EMAIL
import java.time.LocalDateTime
import java.util.*

/**
 * A validation code sent to the end-user identified by [userId].
 * The [reasons] why we are sending the validation code to the end-user determine the [media] we are using.
 */
class ValidationCode(
    /**
     * The validation code the end-user will have to enter.
     *
     * It is stored as a string to accommodate futur validation that may not be integer-based
     * (ex: MFA with authenticator).
     */
    val code: String,

    /**
     * The identifier of the end-user we are sending the validation code to.
     */
    val userId: UUID,

    /**
     * The media used to send the validation code to the end-user. (ex. email)
     */
    val media: ValidationCodeMedia,

    /**
     * List of reason we sent the
     */
    val reasons: List<ValidationCodeReason>,

    /**
     * Identifier of the authorization attempt that requested a validation.
     */
    val attemptId: UUID?,

    val creationDate: LocalDateTime,
    // FIXME val sentDate: LocalDateTime?, ???
    val expirationDate: LocalDateTime
)

/**
 * Enumeration of what we are trying to validation by using a validation code.
 */
enum class ValidationCodeReason(
    val media: ValidationCodeMedia
) {
    /**
     * A validation code has been sent by email to the end-user to verify its email claim.
     */
    EMAIL_CLAIM(EMAIL),
    RESET_PASSWORD(EMAIL),
}

/**
 * Enumeration of media used to send the validation code to the end-user.
 */
enum class ValidationCodeMedia {
    EMAIL
}
