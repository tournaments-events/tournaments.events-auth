package com.sympauthy.api.mapper.admin

import com.sympauthy.api.mapper.config.OutputResourceMapperConfig
import com.sympauthy.api.resource.admin.AdminClientResource
import com.sympauthy.business.model.client.Client
import com.sympauthy.business.model.oauth2.Scope
import org.mapstruct.Mapper

@Mapper(
    config = OutputResourceMapperConfig::class
)
abstract class AdminClientResourceMapper {

    abstract fun toResource(client: Client): AdminClientResource

    fun toScope(scope: Scope): String = scope.scope
}
