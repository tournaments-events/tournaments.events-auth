package tournament.events.auth.data.model

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.serde.annotation.Serdeable
import java.time.LocalDateTime
import java.time.LocalDateTime.now

@Serdeable
@MappedEntity("jwt_keys")
data class JwtKeysEntity(
    val algorithm: String,

    val publicKey: ByteArray,
    val privateKey: ByteArray,

    val creationDate: LocalDateTime = now(),
) {
    @Id
    var name: String? = null
}
