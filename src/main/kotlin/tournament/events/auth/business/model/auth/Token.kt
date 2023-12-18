package tournament.events.auth.business.model.auth

data class Token(
    val accessToken: String,
    val refreshToken: String,

)
