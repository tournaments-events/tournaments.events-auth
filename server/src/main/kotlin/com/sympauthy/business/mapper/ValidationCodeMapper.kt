package com.sympauthy.business.mapper

import com.sympauthy.business.mapper.config.ToBusinessMapperConfig
import com.sympauthy.business.model.code.ValidationCode
import com.sympauthy.data.model.ValidationCodeEntity
import org.mapstruct.Mapper

@Mapper(
    config = ToBusinessMapperConfig::class
)
interface ValidationCodeMapper {

    fun toValidationCode(entity: ValidationCodeEntity): ValidationCode
}
