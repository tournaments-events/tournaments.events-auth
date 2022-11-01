package tournament.events.auth

import io.micronaut.runtime.Micronaut.*
import java.util.*

fun main(args: Array<String>) {
	TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

	build()
		.args(*args)
		.packages("events.tournament.auth")
		.start()
}

