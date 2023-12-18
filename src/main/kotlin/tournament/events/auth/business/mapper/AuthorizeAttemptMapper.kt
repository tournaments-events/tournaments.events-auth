package tournament.events.auth.business.mapper

import org.mapstruct.Mapper
import tournament.events.auth.business.mapper.config.BusinessMapperConfig
import tournament.events.auth.business.model.auth.AuthorizeAttempt
import tournament.events.auth.data.model.AuthorizeAttemptEntity

@Mapper(
    config = BusinessMapperConfig::class
)
interface AuthorizeAttemptMapper {

    fun toAuthorizeAttempt(entity: AuthorizeAttemptEntity): AuthorizeAttempt
}
