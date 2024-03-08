package com.sympauthy.business.mapper

import com.sympauthy.business.mapper.config.ToBusinessMapperConfig
import com.sympauthy.business.model.key.CryptoKeys
import com.sympauthy.data.model.CryptoKeysEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(
    config = ToBusinessMapperConfig::class
)
interface CryptoKeysMapper {

    fun toCryptoKeys(entity: CryptoKeysEntity): CryptoKeys

    @Mappings(
        Mapping(target = "creationDate", expression = "java(java.time.LocalDateTime.now())")
    )
    fun toEntity(key: CryptoKeys): CryptoKeysEntity
}
