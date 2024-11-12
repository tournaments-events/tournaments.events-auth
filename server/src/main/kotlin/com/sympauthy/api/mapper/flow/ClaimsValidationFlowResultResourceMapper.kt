package com.sympauthy.api.mapper.flow

import com.sympauthy.api.mapper.config.OutputResourceMapperConfig
import com.sympauthy.api.resource.flow.ResendClaimsValidationCodesResultResource
import com.sympauthy.api.resource.flow.SendValidationCodeOrGetFlowResultResource
import com.sympauthy.api.resource.flow.ValidationCodeResource
import com.sympauthy.business.model.code.ValidationCode
import com.sympauthy.business.model.code.ValidationCodeMedia
import org.mapstruct.Mapper
import java.net.URI

@Mapper(
    config = OutputResourceMapperConfig::class
)
abstract class ClaimsValidationFlowResultResourceMapper {

    fun toFlowResultResource(validationCode: ValidationCode): SendValidationCodeOrGetFlowResultResource {
        return SendValidationCodeOrGetFlowResultResource(
            code = toResource(validationCode)
        )
    }

    fun toFlowResultResource(redirectUri: URI): SendValidationCodeOrGetFlowResultResource {
        return SendValidationCodeOrGetFlowResultResource(
            redirectUrl = redirectUri.toString()
        )
    }

    fun toResendResultResource(
        media: ValidationCodeMedia,
        resent: Boolean,
        newValidationCode: ValidationCode?
    ): ResendClaimsValidationCodesResultResource {
        return ResendClaimsValidationCodesResultResource(
            media = media.name,
            resent = resent,
            code = newValidationCode?.let(this::toResource)
        )
    }

    abstract fun toResource(validationCode: ValidationCode): ValidationCodeResource
}
