package com.sympauthy.api.mapper.flow

import com.sympauthy.api.mapper.config.OutputResourceMapperConfig
import com.sympauthy.api.resource.flow.ClaimValueResource
import com.sympauthy.api.resource.flow.ClaimsResource
import com.sympauthy.business.model.user.CollectedClaim
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(
    config = OutputResourceMapperConfig::class
)
abstract class ClaimsResourceMapper {

    fun toResource(
        collectedClaims: List<CollectedClaim>
    ): ClaimsResource {
        TODO()
    }

    @Mappings(
        Mapping(target = "claim", source = "collectedClaim.claim.id"),
        Mapping(target = "collected", expression = "java(true)"), // FIXME
        Mapping(target = "suggestedValue", expression = "java(null)") // FIXME
    )
    abstract fun toResource(collectedClaim: CollectedClaim): ClaimValueResource
}
