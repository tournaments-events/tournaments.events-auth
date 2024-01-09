package tournament.events.auth.business.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.Mappings
import tournament.events.auth.business.mapper.config.BusinessMapperConfig
import tournament.events.auth.business.model.user.CollectedUserInfo
import tournament.events.auth.business.model.user.RawUserInfo
import tournament.events.auth.business.model.user.RawUserInfoUpdate
import tournament.events.auth.business.model.user.StandardClaim
import tournament.events.auth.data.model.CollectedUserInfoEntity

@Mapper(
    config = BusinessMapperConfig::class
)
abstract class CollectedUserInfoMapper {

    @Mappings(
        Mapping(target = "collectedInfo", source = "entity"),
        Mapping(target = "info", source = "entity")
    )
    abstract fun toCollectedUserInfo(entity: CollectedUserInfoEntity): CollectedUserInfo

    @Mappings(
        Mapping(target = "subject", source = "userId"),
        Mapping(target = "updatedAt", source = "updateDate"),
    )
    abstract fun toRawUserInfo(entity: CollectedUserInfoEntity): RawUserInfo

    fun toCollectedInfo(entity: CollectedUserInfoEntity): List<StandardClaim> {
        return TODO()
    }

    abstract fun update(
        userInfo: RawUserInfoUpdate,
        @MappingTarget entity: CollectedUserInfoEntity
    ): CollectedUserInfoEntity
}
