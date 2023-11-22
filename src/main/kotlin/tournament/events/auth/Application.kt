package tournament.events.auth

import io.micronaut.runtime.Micronaut.*
import java.util.*

object Application {

	@JvmStatic
	fun main(args: Array<String>) {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

		build()
			.args(*args)
			.packages("events.tournament.auth")
			.start()
	}
}
