package com.sympauthy.business.mapper

import com.sympauthy.business.mapper.config.ToBusinessMapperConfig
import com.sympauthy.business.model.oauth2.AuthorizeAttempt
import com.sympauthy.data.model.AuthorizeAttemptEntity
import org.mapstruct.Mapper

@Mapper(
    config = ToBusinessMapperConfig::class
)
interface AuthorizeAttemptMapper {

    fun toAuthorizeAttempt(entity: AuthorizeAttemptEntity): AuthorizeAttempt
}
