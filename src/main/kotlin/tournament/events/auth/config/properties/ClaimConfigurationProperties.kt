package tournament.events.auth.config.properties

import io.micronaut.context.annotation.EachProperty
import io.micronaut.context.annotation.Parameter
import tournament.events.auth.config.properties.ClaimConfigurationProperties.Companion.CLAIMS_KEY

@EachProperty(CLAIMS_KEY)
class ClaimConfigurationProperties(
    @param:Parameter val id: String
) {
    var enabled: String? = null

    companion object {
        const val CLAIMS_KEY = "claims"
    }
}
