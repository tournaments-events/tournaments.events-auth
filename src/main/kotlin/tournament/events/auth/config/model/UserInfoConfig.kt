package tournament.events.auth.config.model

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.Parameter

/**
 * Configuration for a user info that will be collected by the application:
 * - from a third-party provider if the user uses a provider to authenticate.
 * - or using a form during the user creation.
 *
 * Each information can be either:
 * - required:
 * - optional:
 * - collected: Collected from providers but not asked in the form.
 * - not collected
 */
@ConfigurationProperties("user-info")
class UserInfoConfig(
    @param:Parameter info: String
) {
    var type: String? = null

    /**
     * Reason why the information is collected by the application.
     */
    val reason: Map<String, String>? = null
}
