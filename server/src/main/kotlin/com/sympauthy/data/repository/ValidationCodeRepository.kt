package com.sympauthy.data.repository

import com.sympauthy.data.model.ValidationCodeEntity
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository

interface ValidationCodeRepository : CoroutineCrudRepository<ValidationCodeEntity, String>
