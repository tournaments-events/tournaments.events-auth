package tournament.events.auth.business.mapper

import org.mapstruct.Mapper
import tournament.events.auth.business.mapper.config.ToBusinessMapperConfig
import tournament.events.auth.business.model.oauth2.AuthenticationToken
import tournament.events.auth.data.model.AuthenticationTokenEntity

@Mapper(
    config = ToBusinessMapperConfig::class
)
interface AuthenticationTokenMapper {

    fun toToken(entity: AuthenticationTokenEntity): AuthenticationToken
}
