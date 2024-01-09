package tournament.events.auth.data.model

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.serde.annotation.Serdeable
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Serdeable
@MappedEntity("collected_user_info")
class CollectedUserInfoEntity(
    @get:Id val userId: UUID,
    var collectedBits: ByteArray,

    var name: String? = null,
    var givenName: String? = null,
    var familyName: String? = null,
    var middleName: String? = null,
    var nickname: String? = null,

    var preferredUsername: String? = null,
    var profile: String? = null,
    var picture: String? = null,
    var website: String? = null,

    var email: String? = null,
    var emailVerified: Boolean? = null,

    var gender: String? = null,
    var birthDate: LocalDate? = null,

    var zoneInfo: String? = null,
    var locale: String? = null,

    var phoneNumber: String? = null,
    var phoneNumberVerified: Boolean? = null,

    val creationDate: LocalDateTime,
    var updateDate: LocalDateTime,
)
