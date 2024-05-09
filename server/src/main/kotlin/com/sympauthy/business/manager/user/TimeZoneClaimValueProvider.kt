package com.sympauthy.business.manager.user

import com.sympauthy.business.model.user.claim.Claim
import com.sympauthy.business.model.user.claim.TimeZone
import jakarta.inject.Singleton
import java.time.ZoneId

/**
 *
 */
@Singleton
class TimeZoneClaimValueProvider {

    /**
     * Return the list of allowed [TimeZone] value that can be used by the user for the provided [claim].
     */
    fun listAvailableTimezones(claim: Claim): List<TimeZone> {
        return ZoneId.getAvailableZoneIds().map {
            TimeZone(id = it)
        }
    }
}
