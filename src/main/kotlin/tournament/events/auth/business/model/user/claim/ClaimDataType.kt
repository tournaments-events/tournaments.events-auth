package tournament.events.auth.business.model.user.claim

import kotlin.reflect.KClass

/**
 * Enumeration of supported data type for a user claim.
 */
enum class ClaimDataType(
    val typeClass: KClass<*>
) {
    STRING(String::class)
}
