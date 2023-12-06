package tournament.events.auth.business.manager.mapper

import org.mapstruct.Mapper
import tournament.events.auth.business.manager.mapper.config.BusinessMapperConfig
import tournament.events.auth.business.model.jwt.JwtKeys
import tournament.events.auth.data.model.JwtKeysEntity

@Mapper(
    config = BusinessMapperConfig::class
)
interface JwtKeysMapper {

    fun toJwtKeys(entity: JwtKeysEntity): JwtKeys
}
