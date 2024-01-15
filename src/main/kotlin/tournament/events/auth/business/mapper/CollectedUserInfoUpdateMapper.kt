package tournament.events.auth.business.mapper

import org.mapstruct.*
import tournament.events.auth.business.mapper.config.ToEntityMapperConfig
import tournament.events.auth.business.model.user.CollectedUserInfoUpdate
import tournament.events.auth.business.model.user.claim.Claim
import tournament.events.auth.data.model.CollectedUserInfoEntity
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
        update: CollectedUserInfoUpdate
    ): CollectedUserInfoEntity

    @Mappings(
        Mapping(target = "userId", ignore = true),
        Mapping(target = "verified", ignore = true),
        Mapping(target = "collectionDate", expression = "java(java.time.LocalDateTime.now())")
    )
    @InheritInverseConfiguration
    abstract fun updateEntity(
        @MappingTarget entity: CollectedUserInfoEntity,
        update: CollectedUserInfoUpdate
    ): CollectedUserInfoEntity

    fun toClaim(claim: Claim): String = claim.id

    fun toValue(value: Optional<Any>?): String? {
        return value?.getOrNull()?.let { claimValueMapper.toEntity(it) }
    }
}
