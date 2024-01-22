package com.sympauthy.business.mapper

import com.sympauthy.business.mapper.config.ToBusinessMapperConfig
import com.sympauthy.business.model.oauth2.AuthorizationCode
import com.sympauthy.data.model.AuthorizationCodeEntity
import org.mapstruct.Mapper

@Mapper(
    config = ToBusinessMapperConfig::class
)
interface AuthorizationCodeMapper {

    fun toAuthorizationCode(entity: AuthorizationCodeEntity): AuthorizationCode
}
