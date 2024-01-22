package com.sympauthy.business.mapper.config

import org.mapstruct.MapperConfig
import org.mapstruct.NullValuePropertyMappingStrategy
import org.mapstruct.ReportingPolicy

/**
 * Configuration for mappers that are converting entities into business object.
 */
@MapperConfig(
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
class ToBusinessMapperConfig
