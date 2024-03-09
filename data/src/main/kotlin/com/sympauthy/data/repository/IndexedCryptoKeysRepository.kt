package com.sympauthy.data.repository

import com.sympauthy.data.model.IndexedCryptoKeysEntity
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository

interface IndexedCryptoKeysRepository : CoroutineCrudRepository<IndexedCryptoKeysEntity, Int> {

    suspend fun findByNameAndAlgorithm(name: String, algorithm: String): List<IndexedCryptoKeysEntity>
}
