package com.sympauthy.business.mapper

import com.sympauthy.business.mapper.config.ToEntityMapperConfig
import com.sympauthy.business.model.user.CollectedClaimUpdate
import com.sympauthy.business.model.user.claim.Claim
import com.sympauthy.data.model.CollectedClaimEntity
import org.mapstruct.*
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Mapper(
    config = ToEntityMapperConfig::class
)
abstract class CollectedUserInfoUpdateMapper {

    lateinit var claimValueMapper: ClaimValueMapper

    @Mappings(
        Mapping(target = "verified", expression = "java(null)"),
        Mapping(target = "collectionDate", expression = "java(java.time.LocalDateTime.now())")
    )
    @InheritInverseConfiguration
    abstract fun toEntity(
        userId: UUID,
        update: CollectedClaimUpdate
    ): CollectedClaimEntity

    @Mappings(
        Mapping(target = "userId", ignore = true),
        Mapping(target = "verified", ignore = true),
        Mapping(target = "collectionDate", expression = "java(java.time.LocalDateTime.now())")
    )
    @InheritInverseConfiguration
    abstract fun updateEntity(
        @MappingTarget entity: CollectedClaimEntity,
        update: CollectedClaimUpdate
    ): CollectedClaimEntity

    fun toClaim(claim: Claim): String = claim.id

    fun toValue(value: Optional<Any>?): String? {
        return value?.getOrNull()?.let { claimValueMapper.toEntity(it) }
    }
}
