package tournament.events.auth.business.mapper

import org.mapstruct.Mapper
import tournament.events.auth.business.mapper.config.BusinessMapperConfig
import tournament.events.auth.business.model.auth.oauth2.EncodedAuthenticationToken
import tournament.events.auth.data.model.AuthenticationTokenEntity

@Mapper(
    config = BusinessMapperConfig::class
)
interface EncodedAuthenticationTokenMapper {

    fun toEncodedAuthenticationToken(
        entity: AuthenticationTokenEntity,
        token: String
    ): EncodedAuthenticationToken
}
