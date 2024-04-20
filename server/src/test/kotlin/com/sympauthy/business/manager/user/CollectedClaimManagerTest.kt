package com.sympauthy.business.manager.user

import com.sympauthy.business.manager.ClaimManager
import com.sympauthy.business.mapper.CollectedClaimMapper
import com.sympauthy.business.mapper.CollectedUserInfoUpdateMapper
import com.sympauthy.business.model.user.CollectedClaim
import com.sympauthy.business.model.user.claim.Claim
import com.sympauthy.data.repository.CollectedClaimRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CollectedClaimManagerTest {

    @MockK
    lateinit var claimManager: ClaimManager

    @MockK
    lateinit var collectedClaimRepository: CollectedClaimRepository

    @MockK
    lateinit var collectedClaimMapper: CollectedClaimMapper

    @MockK
    lateinit var collectedClaimUpdateMapper: CollectedUserInfoUpdateMapper

    @InjectMockKs
    lateinit var manager: CollectedClaimManager

    @Test
    fun `areAllRequiredClaimCollected - True if all required claims are collected false otherwise`() {
        val firstRequiredClaim = mockk<Claim>()
        val secondRequiredClaim = mockk<Claim>()
        val optionalClaim = mockk<Claim>()

        val firstRequiredCollectedClaim = mockk<CollectedClaim> {
            every { claim } returns firstRequiredClaim
        }
        val secondRequiredCollectedClaim = mockk<CollectedClaim> {
            every { claim } returns secondRequiredClaim
        }
        val optionalCollectedClaim = mockk<CollectedClaim> {
            every { claim } returns optionalClaim
        }

        every { claimManager.listRequiredClaims() } returns listOf(firstRequiredClaim, secondRequiredClaim)

        assertTrue(
            manager.areAllRequiredClaimCollected(
                listOf(
                    firstRequiredCollectedClaim,
                    secondRequiredCollectedClaim
                )
            )
        )

        assertFalse(manager.areAllRequiredClaimCollected(listOf()))
        assertFalse(manager.areAllRequiredClaimCollected(listOf(firstRequiredCollectedClaim)))
        assertFalse(manager.areAllRequiredClaimCollected(listOf(secondRequiredCollectedClaim)))
        assertFalse(manager.areAllRequiredClaimCollected(listOf(optionalCollectedClaim)))
    }

    @Test
    fun `areAllRequiredClaimCollected - Always true if not required claims`() {
        every { claimManager.listRequiredClaims() } returns emptyList()

        assertTrue(manager.areAllRequiredClaimCollected(listOf()))
        assertTrue(manager.areAllRequiredClaimCollected(listOf(mockk())))
    }
}