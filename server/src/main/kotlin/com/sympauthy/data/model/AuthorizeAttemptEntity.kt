package com.sympauthy.data.model

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.serde.annotation.Serdeable
import java.time.LocalDateTime
import java.util.*

@Serdeable
@MappedEntity("authorize_attempts")
class AuthorizeAttemptEntity(
    val clientId: String,
    val redirectUri: String,
    val scopes: Array<String> = emptyArray(),
    val state: String? = null,
    val userId: UUID? = null,
    val attemptDate: LocalDateTime,
    val expirationDate: LocalDateTime
) {
    @Id
    @GeneratedValue
    var id: UUID? = null
}
