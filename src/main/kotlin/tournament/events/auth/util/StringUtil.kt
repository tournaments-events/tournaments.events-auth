package tournament.events.auth.util

fun String?.nullIfBlank(): String? = if (!this.isNullOrBlank()) this else null
