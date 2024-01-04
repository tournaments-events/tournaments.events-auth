package tournament.events.auth.api.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import tournament.events.auth.api.mapper.config.OutputResourceMapperConfig
import tournament.events.auth.api.resource.openid.UserInfoResource
import tournament.events.auth.business.model.user.RawUserInfo
import java.time.LocalDateTime
import java.time.ZoneOffset

@Mapper(
    config = OutputResourceMapperConfig::class
)
abstract class UserInfoResourceMapper {

    @Mappings(
        Mapping(target = "sub", source = "subject")
    )
    abstract fun toResource(info: RawUserInfo): UserInfoResource

    fun toUpdatedAt(updatedAt: LocalDateTime?): Long? {
        return updatedAt?.toInstant(ZoneOffset.UTC)?.epochSecond
    }
}
