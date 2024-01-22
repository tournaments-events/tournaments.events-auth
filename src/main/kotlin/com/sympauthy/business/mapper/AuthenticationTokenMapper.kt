package com.sympauthy.business.mapper

import com.sympauthy.business.mapper.config.ToBusinessMapperConfig
import com.sympauthy.business.model.oauth2.AuthenticationToken
import com.sympauthy.data.model.AuthenticationTokenEntity
import org.mapstruct.Mapper

@Mapper(
    config = ToBusinessMapperConfig::class
)
interface AuthenticationTokenMapper {

    fun toToken(entity: AuthenticationTokenEntity): AuthenticationToken
}
