package tournament.events.auth.business.manager.jwt

import jakarta.inject.Inject
import jakarta.inject.Named
import jakarta.inject.Singleton
import tournament.events.auth.business.mapper.CryptoKeysMapper
import tournament.events.auth.business.mapper.IndexedCryptoKeysMapper
import tournament.events.auth.business.model.key.CryptoKeys
import tournament.events.auth.business.model.key.KeyAlgorithm
import tournament.events.auth.data.model.IndexedCryptoKeysEntity
import tournament.events.auth.data.repository.CryptoKeysRepository
import tournament.events.auth.data.repository.IndexedCryptoKeysRepository

/**
 * A negotiation protocol using the database to determine which keys to use.
 *
 * It relies on a sequence of autoincrement id.
 *
 * Detailed protocol is:
 * - Each instance looks into the database if a key exists.
 * - If the key exists, the instance takes the lowest autoincrement id.
 * - If the key does not exist:
 *   - The instance insert a newly generated key.
 *   - Then read all existing keys.
 *   - The instance takes the one with the lowest autoincrement id.
 */
@Singleton
@Named("autoincrement")
class AutoIncrementCryptoKeysGenerationStrategy(
    @Inject private val keysRepository: CryptoKeysRepository,
    @Inject private val indexedKeysRepository: IndexedCryptoKeysRepository,
    @Inject private val keysMapper: CryptoKeysMapper,
    @Inject private val indexedKeysMapper: IndexedCryptoKeysMapper,
) : CryptoKeysGenerationStrategy {

    override suspend fun generateKeys(
        name: String,
        algorithm: KeyAlgorithm
    ): CryptoKeys {
        var existingKeys = indexedKeysRepository.findByNameAndAlgorithm(name, algorithm.name)
        // If the key exists, we take the one that was persisted in the database as all the instances will see it.
        if (existingKeys.isNotEmpty()) {
            return existingKeys.sortedBy(IndexedCryptoKeysEntity::index)
                .first()
                .let(indexedKeysMapper::toCryptoKeys)
        }

        // Otherwise we create our own key and persist it inside the database.
        val key = algorithm.impl.generate(name)
        val indexedKeyEntity = indexedKeysRepository.save(indexedKeysMapper.toEntity(key))

        // Finally we check if we were the first to persist our key.
        existingKeys = indexedKeysRepository.findByNameAndAlgorithm(name, algorithm.name)
        val firstKey = existingKeys.sortedBy(IndexedCryptoKeysEntity::index).first()
        return if (firstKey.index == indexedKeyEntity.index) {
            // If we are the instance that created the key, we persist it inside the crypto_keys table
            keysRepository.save(keysMapper.toEntity(key))
            key
        } else {
            // If we are not the instance that created the key first,
            // we delete our key and use the one with the lowest id.
            indexedKeysRepository.delete(indexedKeyEntity)
            indexedKeysMapper.toCryptoKeys(firstKey)
        }
    }
}
