package tournament.events.auth.business.manager.jwt

import jakarta.inject.Named
import jakarta.inject.Singleton

/**
 *
 *
 * Key negotiation protocol:
 * - Each replica looks into the database if a key exists.
 * - If the key exists, the replica takes the lowest autoincrement id.
 * - If the key does not exist:
 *   - The replica insert a new generated key with a random UUID. The timestamp is leaved to the database to generate.
 *   - Then read all existing keys.
 *   - The replica takes the lowest autoincrement id.
 *   - Then delete the key it inserted if it is not the one it created.
 *
 * This negotiation protocol relies on  the sequence of autoincrement key being consistant on SQL.
 */
@Singleton
@Named("autoincrement")
class AutoIncrementKeyGenerationStrategy(

) : KeyGenerationStrategy {

    override suspend fun getKey(name: String): String {
        TODO("Not yet implemented")
    }
}
