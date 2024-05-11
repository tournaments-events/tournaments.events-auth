package com.sympauthy.api.mapper.flow

import com.sympauthy.api.mapper.config.OutputResourceMapperConfig
import com.sympauthy.api.resource.provider.TimeZoneResource
import com.sympauthy.business.model.user.claim.TimeZone
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Mapper(
    config = OutputResourceMapperConfig::class
)
abstract class TimeZoneResourceMapper {

    private val offsetFormatter = DateTimeFormatter.ofPattern("xxx")

    fun toResources(timeZones: List<TimeZone>): List<TimeZoneResource> {
        val now = LocalDateTime.now()
        return timeZones
            .map {
                toResource(
                    timeZone = it,
                    now = now
                )
            }
            .sortedBy(TimeZoneResource::id)
    }

    @Mappings(
        Mapping(target = "id", source = "timeZone.zone.id"),
        Mapping(target = "offset", expression = "java(toOffset(timeZone, now))"),
    )
    abstract fun toResource(
        timeZone: TimeZone,
        now: LocalDateTime
    ): TimeZoneResource

    fun toOffset(
        timeZone: TimeZone,
        now: LocalDateTime
    ): String {
        return timeZone.zone.rules.getOffset(now)
            .let(offsetFormatter::format)
    }
}
