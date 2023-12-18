package tournament.events.auth.business.mapper

import org.mapstruct.InheritInverseConfiguration
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import tournament.events.auth.business.mapper.config.BusinessMapperConfig
import tournament.events.auth.business.model.provider.ProviderUserInfo
import tournament.events.auth.business.model.provider.RawProviderUserInfo
import tournament.events.auth.data.model.ProviderUserInfoEntity
import tournament.events.auth.data.model.ProviderUserInfoEntityId
import java.time.LocalDateTime
import java.util.UUID

@Mapper(
    config = BusinessMapperConfig::class
)
interface ProviderUserInfoMapper {

    @Mappings(
        Mapping(target = "providerId", source = "id.providerId"),
        Mapping(target = "userId", source = "id.userId"),
        Mapping(target = "userInfo", source = "entity")
    )
    fun toProviderUserInfo(entity: ProviderUserInfoEntity): ProviderUserInfo

    fun toRawProviderUserInfo(entity: ProviderUserInfoEntity): RawProviderUserInfo

    @InheritInverseConfiguration
    @Mappings(
        Mapping(target = "id", expression = "java(toEntityId(providerId, userId))")
    )
    fun toEntity(
        providerId: String,
        userId: UUID,
        userInfo: RawProviderUserInfo,
        lastFetchDate: LocalDateTime,
        lastChangeDate: LocalDateTime
    ): ProviderUserInfoEntity

    fun toEntityId(
        providerId: String,
        userId: UUID
    ): ProviderUserInfoEntityId
}
