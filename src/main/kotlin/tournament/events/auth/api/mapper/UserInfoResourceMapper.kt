package tournament.events.auth.api.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import tournament.events.auth.api.mapper.config.ApiMapperConfig
import tournament.events.auth.api.model.openid.UserInfoResource
import tournament.events.auth.business.model.user.RawUserInfo

@Mapper(
    config = ApiMapperConfig::class
)
interface UserInfoResourceMapper {

    @Mappings(
        Mapping(target = "sub", source = "subject")
    )
    fun toResource(info: RawUserInfo): UserInfoResource
}
