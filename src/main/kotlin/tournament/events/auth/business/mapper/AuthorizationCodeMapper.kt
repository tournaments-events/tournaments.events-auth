package tournament.events.auth.business.mapper

import org.mapstruct.Mapper
import tournament.events.auth.business.mapper.config.ToBusinessMapperConfig
import tournament.events.auth.business.model.oauth2.AuthorizationCode
import tournament.events.auth.data.model.AuthorizationCodeEntity

@Mapper(
    config = ToBusinessMapperConfig::class
)
interface AuthorizationCodeMapper {

    fun toAuthorizationCode(entity: AuthorizationCodeEntity): AuthorizationCode
}
