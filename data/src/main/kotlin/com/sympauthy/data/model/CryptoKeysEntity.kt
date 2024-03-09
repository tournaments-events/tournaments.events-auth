package com.sympauthy.data.model

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.serde.annotation.Serdeable
import java.time.LocalDateTime
import java.time.LocalDateTime.now

@Serdeable
@MappedEntity("crypto_keys")
data class CryptoKeysEntity(
    val algorithm: String,

    val publicKey: ByteArray?,
    val publicKeyFormat: String?,

    val privateKey: ByteArray,
    val privateKeyFormat: String?,

    val creationDate: LocalDateTime = now(),
) {
    @Id
    var name: String? = null
}
