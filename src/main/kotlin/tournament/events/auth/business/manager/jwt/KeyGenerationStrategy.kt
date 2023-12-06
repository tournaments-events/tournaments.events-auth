package tournament.events.auth.business.manager.jwt

/**
 * This interface describe a
 */
interface KeyGenerationStrategy {

    /**
     * Return the key associated to the [name] that is shared across all instances of this application.
     *
     * If the key,
     */
    suspend fun getKey(name: String): String
}
