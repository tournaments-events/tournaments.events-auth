package com.sympauthy.api.mapper

import com.sympauthy.api.mapper.config.OutputResourceMapperConfig
import com.sympauthy.api.resource.openid.UserInfoResource
import com.sympauthy.business.model.user.RawUserInfo
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import java.time.LocalDateTime
import java.time.ZoneOffset

@Mapper(
    config = OutputResourceMapperConfig::class
)
abstract class UserInfoResourceMapper {

    @Mappings(
        Mapping(target = "sub", source = "subject")
    )
    abstract fun toResource(info: RawUserInfo): UserInfoResource

    fun toUpdatedAt(updatedAt: LocalDateTime?): Long? {
        return updatedAt?.toInstant(ZoneOffset.UTC)?.epochSecond
    }
}
