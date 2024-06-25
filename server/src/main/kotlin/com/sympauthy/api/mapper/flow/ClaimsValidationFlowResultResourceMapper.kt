package com.sympauthy.api.mapper.flow

import com.sympauthy.api.mapper.config.OutputResourceMapperConfig
import com.sympauthy.api.resource.flow.ClaimsValidationFlowResultResource
import com.sympauthy.api.resource.flow.ValidationCodeResource
import com.sympauthy.business.model.code.ValidationCode
import org.mapstruct.Mapper
import java.net.URI

@Mapper(
    config = OutputResourceMapperConfig::class
)
abstract class ClaimsValidationFlowResultResourceMapper {

    fun toResource(validationCodes: List<ValidationCode>): ClaimsValidationFlowResultResource {
        return ClaimsValidationFlowResultResource(
            codes = validationCodes.map(this::toResource)
        )
    }

    fun toResource(redirectUri: URI): ClaimsValidationFlowResultResource {
        return ClaimsValidationFlowResultResource(
            redirectUrl = redirectUri.toString()
        )
    }

    abstract fun toResource(validationCode: ValidationCode): ValidationCodeResource
}
