package com.sympauthy.business.mapper

import com.sympauthy.business.mapper.config.ToBusinessMapperConfig
import com.sympauthy.business.model.oauth2.EncodedAuthenticationToken
import com.sympauthy.data.model.AuthenticationTokenEntity
import org.mapstruct.Mapper

@Mapper(
    config = ToBusinessMapperConfig::class
)
interface EncodedAuthenticationTokenMapper {

    fun toEncodedAuthenticationToken(
        entity: AuthenticationTokenEntity,
        token: String
    ): EncodedAuthenticationToken
}
