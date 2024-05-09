package com.sympauthy.api.controller.flow

import com.sympauthy.api.resource.provider.TimeZoneResource
import com.sympauthy.api.util.orNotFound
import com.sympauthy.business.manager.ClaimManager
import com.sympauthy.business.manager.user.TimeZoneClaimValueProvider
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.Operation
import jakarta.inject.Inject

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/api/v1/flow/claims/{claimId}/timezones")
class TimeZoneController(
    @Inject private val claimManager: ClaimManager,
    @Inject private val timeZoneProvider: TimeZoneClaimValueProvider
) {

    @Get
    @Operation(
        summary = "List timezones",
        description = """
List all timezones that are allowed to be selected for the claim.

The list of timezones will depends on:
- the version of the TZ database integrated into the authorization server.
- the allowed value declared for the claim in the configuration.
        """
    )
    fun listTimeZones(
        @PathVariable claimId: String
    ): List<TimeZoneResource> {
        val claim = claimManager.findById(claimId).orNotFound()
        return timeZoneProvider.listAvailableTimezones(claim).map {
            TimeZoneResource(it.id)
        }
    }
}
