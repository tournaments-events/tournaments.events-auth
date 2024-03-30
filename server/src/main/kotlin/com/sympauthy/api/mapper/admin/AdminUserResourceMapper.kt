package com.sympauthy.api.mapper.admin

import com.sympauthy.api.mapper.config.OutputResourceMapperConfig
import com.sympauthy.api.resource.admin.AdminUserResource
import com.sympauthy.business.model.user.User
import org.mapstruct.Mapper

@Mapper(
    config = OutputResourceMapperConfig::class
)
interface AdminUserResourceMapper {

    fun toResource(user: User): AdminUserResource
}
