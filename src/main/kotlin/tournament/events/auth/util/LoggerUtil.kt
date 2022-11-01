package tournament.events.auth.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Suppress("unused")
inline fun <reified T : Any> T.loggerForClass(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}
