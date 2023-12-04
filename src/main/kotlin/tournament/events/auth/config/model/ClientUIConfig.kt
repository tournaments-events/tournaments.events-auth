package tournament.events.auth.config.model

import io.micronaut.serde.annotation.Serdeable

/**
 * Configuration containing all the elements required to display the
 */
@Serdeable
data class ClientUIConfig(
    /**
     * CSS code of the color to use for the background when displaying a button redirecting
     * to the
     */
    val buttonBackground: String?,
    /**
     * CSS code of the color to use as the text when displaying a button redirecting
     */
    val buttonText: String?
)
