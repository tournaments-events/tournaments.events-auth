package tournament.events.auth.business.mapper

import org.mapstruct.Mapper
import tournament.events.auth.business.mapper.config.BusinessMapperConfig
import tournament.events.auth.business.model.auth.oauth2.AuthorizationCode
import tournament.events.auth.data.model.AuthorizationCodeEntity

@Mapper(
    config = BusinessMapperConfig::class
)
interface AuthorizationCodeMapper {

    fun toAuthorizationCode(entity: AuthorizationCodeEntity): AuthorizationCode
}
