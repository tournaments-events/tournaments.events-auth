package com.sympauthy.api.mapper.flow

import com.sympauthy.api.mapper.config.OutputResourceMapperConfig
import com.sympauthy.api.resource.flow.ClaimValidationResultResource
import com.sympauthy.api.resource.flow.ClaimsValidationResource
import com.sympauthy.api.resource.flow.ValidationCodeResource
import com.sympauthy.business.model.code.ValidationCode
import org.mapstruct.Mapper
import java.net.URI

@Mapper(
    config = OutputResourceMapperConfig::class
)
abstract class ClaimValidationResourceMapper {

    fun toResource(validationCodes: List<ValidationCode>): ClaimsValidationResource {
        return ClaimsValidationResource(
            codes = validationCodes.map(::toResource)
        )
    }

    fun toResultResource(validationCodes: List<ValidationCode>): ClaimValidationResultResource {
        return ClaimValidationResultResource(
            codes = validationCodes.map(::toResource)
        )
    }

    fun toResultResource(redirectUri: URI): ClaimValidationResultResource {
        return ClaimValidationResultResource(
            redirectUrl = redirectUri.toString()
        )
    }

    abstract fun toResource(validationCode: ValidationCode): ValidationCodeResource
}
