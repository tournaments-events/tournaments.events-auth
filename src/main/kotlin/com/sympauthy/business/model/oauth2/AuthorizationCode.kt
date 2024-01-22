package com.sympauthy.business.model.oauth2

import java.time.LocalDateTime
import java.util.*

data class AuthorizationCode(
    val attemptId: UUID,
    val code: String,
    val creationDate: LocalDateTime,
    val expirationDate: LocalDateTime
)
