package tournament.events.auth.data.model

import org.komapper.annotation.*
import java.time.LocalDateTime
import java.time.LocalDateTime.now

data class LoginAttemptEntity(
    val state: String,
    val redirectUri: String,
    val creationDate: LocalDateTime = now(),
    var updateDate: LocalDateTime = now()
)

@Suppress("unused")
@KomapperEntityDef(LoginAttemptEntity::class)
@KomapperTable("login_attempts")
data class LoginAttemptEntityDef(
    @KomapperId val state: Nothing,
    @KomapperColumn("redirect_uri") val redirectUri: Nothing,
    @KomapperCreatedAt @KomapperColumn("creation_date") val creationDate: Nothing,
    @KomapperUpdatedAt @KomapperColumn("update_date") val updateDate: Nothing
)
