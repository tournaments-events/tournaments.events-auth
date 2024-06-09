package com.sympauthy.api.mapper.flow

import com.sympauthy.api.mapper.config.OutputResourceMapperConfig
import com.sympauthy.api.resource.flow.ClaimsValidationResource
import com.sympauthy.api.resource.flow.ValidationCodeResource
import com.sympauthy.business.model.code.ValidationCode
import org.mapstruct.Mapper

@Mapper(
    config = OutputResourceMapperConfig::class
)
abstract class ValidationCodesResourceMapper {

    fun toResource(validationCodes: List<ValidationCode>): ClaimsValidationResource {
        return ClaimsValidationResource(
            codes = validationCodes.map(::toResource)
        )
    }

    abstract fun toResource(validationCode: ValidationCode): ValidationCodeResource
}
