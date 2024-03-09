package com.sympauthy.data.repository

import com.sympauthy.data.model.CryptoKeysEntity
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository

interface CryptoKeysRepository : CoroutineCrudRepository<CryptoKeysEntity, String> {

    suspend fun findByName(name: String): CryptoKeysEntity?
}
