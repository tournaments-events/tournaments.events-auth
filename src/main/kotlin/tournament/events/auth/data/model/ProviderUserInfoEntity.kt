package tournament.events.auth.data.model

import io.micronaut.data.annotation.Embeddable
import io.micronaut.data.annotation.EmbeddedId
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.serde.annotation.Serdeable
import java.time.LocalDate
import java.time.LocalDateTime

@Embeddable
data class ProviderUserInfoEntityId(
    val providerId: String,
    val userId: String,
)

@Serdeable
@MappedEntity("provider_user_info")
data class ProviderUserInfoEntity(
    @EmbeddedId val id: ProviderUserInfoEntityId,

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

    val lastUpdateDate: LocalDateTime? = null
)
