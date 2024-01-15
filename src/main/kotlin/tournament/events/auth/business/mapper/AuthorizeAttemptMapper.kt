package tournament.events.auth.business.mapper

import org.mapstruct.Mapper
import tournament.events.auth.business.mapper.config.ToBusinessMapperConfig
import tournament.events.auth.business.model.oauth2.AuthorizeAttempt
import tournament.events.auth.data.model.AuthorizeAttemptEntity

@Mapper(
    config = ToBusinessMapperConfig::class
)
interface AuthorizeAttemptMapper {

    fun toAuthorizeAttempt(entity: AuthorizeAttemptEntity): AuthorizeAttempt
}
