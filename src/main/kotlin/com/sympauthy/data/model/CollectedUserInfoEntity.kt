package com.sympauthy.data.model

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.serde.annotation.Serdeable
import java.time.LocalDateTime
import java.util.*

@Serdeable
@MappedEntity("collected_user_info")
class CollectedUserInfoEntity(
    @get:Id var userId: UUID,
    var claim: String,
    var value: String?,
    var verified: Boolean?,
    var collectionDate: LocalDateTime
)
