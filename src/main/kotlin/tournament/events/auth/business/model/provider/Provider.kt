package tournament.events.auth.business.model.provider

import tournament.events.auth.business.exception.BusinessException
import tournament.events.auth.business.model.provider.config.ProviderAuth

/**
 * A third-party authentication provider.
 */
sealed class Provider(
    val id: String,
    val enable: Boolean
)

/**
 * A [Provider] that we can call for authentication as we did not find any evident defect in the configuration.
 */
class EnabledProvider(
    id: String,
    val name: String,
    val auth: ProviderAuth
): Provider(id, true)

/**
 * A [Provider] that have an evident defect in its configuration.
 * The [cause] holds the reason why we failed to configure this [Provider].
 */
class DisabledProvider(
    id: String,
    val cause: BusinessException
): Provider(id, false)
