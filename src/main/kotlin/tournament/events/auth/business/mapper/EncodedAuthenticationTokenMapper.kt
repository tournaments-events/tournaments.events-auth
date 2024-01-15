package tournament.events.auth.business.mapper

import org.mapstruct.Mapper
import tournament.events.auth.business.mapper.config.ToBusinessMapperConfig
import tournament.events.auth.business.model.oauth2.EncodedAuthenticationToken
import tournament.events.auth.data.model.AuthenticationTokenEntity

@Mapper(
    config = ToBusinessMapperConfig::class
)
interface EncodedAuthenticationTokenMapper {

    fun toEncodedAuthenticationToken(
        entity: AuthenticationTokenEntity,
        token: String
    ): EncodedAuthenticationToken
}
