package tournament.events.auth.business.manager.jwt

import com.auth0.jwt.algorithms.Algorithm
import io.micronaut.http.HttpStatus
import io.micronaut.http.HttpStatus.INTERNAL_SERVER_ERROR
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.rx3.await
import kotlinx.coroutines.rx3.rxMaybe
import tournament.events.auth.business.exception.businessExceptionOf
import tournament.events.auth.business.exception.orMissingConfig
import tournament.events.auth.business.manager.mapper.JwtKeysMapper
import tournament.events.auth.business.model.jwt.JwtAlgorithm
import tournament.events.auth.business.model.jwt.JwtKeys
import tournament.events.auth.config.model.JwtConfig
import tournament.events.auth.data.repository.JwtKeysRepository
import tournament.events.auth.util.enumValueOfOrNull


@Singleton
class KeyManager(
    @Inject private val jwtConfig: JwtConfig,
    @Inject private val keysRepository: JwtKeysRepository,
    @Inject private val jwtKeysMapper: JwtKeysMapper
) {
    private val keysMap = mutableMapOf<String, Single<JwtKeys>>()

    fun getJwtAlgorithm(): JwtAlgorithm {
        return enumValueOfOrNull<JwtAlgorithm>(jwtConfig.algorithm.orMissingConfig("jwt.algorithm"))
            ?: throw businessExceptionOf(
                INTERNAL_SERVER_ERROR, "exception.jwt.unsupported_algorithm",
                "algorithm" to (jwtConfig.algorithm ?: ""),
                "algorithms" to JwtAlgorithm.values().joinToString(", ")
            )
    }

    /**
     * Return the [Algorithm] initialized with the signing key named [name].
     *
     * If the key does not exist in the database or has not been configured in the application.yml,
     * then it will be generated according to the key generation strategy.
     */
    suspend fun getAlgorithm(name: String): Algorithm {
        val algorithm = getJwtAlgorithm()
        val key = getKey(name, algorithm)
        return algorithm.behavior.initializeWithKeys(key)
    }

    internal suspend fun getKey(name: String, algorithm: JwtAlgorithm): JwtKeys {
        // Get key and return if found.
        val existingKey = keysMap[name]?.await()
        if (existingKey != null) {
            return existingKey
        }

        val key = synchronized(keysMap) {
            // Synchronized check to see if it has not been created by another worker thread.
            val existingKeySingle = keysMap[name]
            if (existingKeySingle != null) {
                existingKeySingle
            } else {
                // Otherwise create the single that will generate the key.
                // Cache the key and store it into the map to be reused.
                val newKeySingle = getOrGenerateKey(name, algorithm).cache()
                keysMap[name] = newKeySingle
                newKeySingle
            }
        }
        return key.await()
    }

    internal fun getOrGenerateKey(name: String, algorithm: JwtAlgorithm): Single<JwtKeys> {
        return getStoredKey(name, algorithm).toSingle()
    }

    internal fun getStoredKey(name: String, algorithm: JwtAlgorithm): Maybe<JwtKeys> {
        return rxMaybe {
            val existingKey = keysRepository.findByName(name)
            if (existingKey != null) {
                if (existingKey.algorithm != algorithm.name) {
                    keysRepository.delete(existingKey)
                    null
                } else existingKey
            } else null
        }.map(jwtKeysMapper::toJwtKeys)
    }
}
