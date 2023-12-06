package tournament.events.auth.util

inline fun <reified T : Enum<T>> enumValueOfOrNull(value: String?): T? {
    return try {
        value?.let { enumValueOf<T>(it) }
    } catch (e: IllegalArgumentException) {
        null
    }
}
