package com.sympauthy.business.mapper

import com.sympauthy.business.mapper.config.ToBusinessMapperConfig
import com.sympauthy.business.model.user.User
import com.sympauthy.data.model.UserEntity
import org.mapstruct.Mapper

@Mapper(
    config = ToBusinessMapperConfig::class
)
interface UserMapper {

    fun toUser(entity: UserEntity): User
}
