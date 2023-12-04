package tournament.events.auth.business.manager.auth

/**
 * Key negociation protocol:
 * - Each replica looks into the database if a key exists.
 * - If the key exists, the replica takes the oldest generated key.
 * - If the key does not exist:
 *   - The replica insert a new generated key with a random UUID. The timestamp is leaved to the database to generate.
 *   - Then read all existing keys.
 *   - The replica takes the oldest generated key.
 *   - Then delete the key it inserted if it is not the one it created.
 *
 * This negociation protocol assumes the read and write to the database are done on the same database instance as
 * we will rely on the consistancy on the read and the write on this instance.
 */
class KeyManager {
}
