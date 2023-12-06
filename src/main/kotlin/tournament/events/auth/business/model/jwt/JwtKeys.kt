package tournament.events.auth.business.model.jwt

data class JwtKeys(
    val algorithm: String,
    val publicKey: ByteArray?,
    val privateKey: ByteArray
)
