package tournament.events.auth.data.model

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.serde.annotation.Serdeable
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Serdeable
@MappedEntity("collected_user_info")
class CollectedUserInfoEntity(
    @get:Id val userId: UUID,
    val collectedBits: ByteArray,

    val name: String? = null,
    val givenName: String? = null,
    val familyName: String? = null,
    val middleName: String? = null,
    val nickname: String? = null,

    val preferredUsername: String? = null,
    val profile: String? = null,
    val picture: String? = null,
    val website: String? = null,

    val email: String? = null,
    val emailVerified: Boolean? = null,

    val gender: String? = null,
    val birthDate: LocalDate? = null,

    val zoneInfo: String? = null,
    val locale: String? = null,

    val phoneNumber: String? = null,
    val phoneNumberVerified: Boolean? = null,

    val creationDate: LocalDateTime,
    val updateDate: LocalDate,
)
