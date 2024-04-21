package com.sympauthy.business.manager

import com.sympauthy.business.manager.user.CollectedClaimManager
import com.sympauthy.business.mapper.ClaimValueMapper
import com.sympauthy.data.repository.CollectedClaimRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class SignUpFlowManagerTest {

    @MockK
    lateinit var collectedClaimManager: CollectedClaimManager

    @MockK
    lateinit var collectedClaimRepository: CollectedClaimRepository

    @MockK
    lateinit var claimValueMapper: ClaimValueMapper

    @InjectMockKs
    lateinit var manager: SignUpFlowManager

    @Test
    fun `checkIfSignUpIsComplete - Non complete if missing claims`()= runTest {
        every { collectedClaimManager.areAllRequiredClaimCollected(any()) } returns false

        val result = manager.checkIfSignUpIsComplete(mockk(), mockk())

        assertFalse(result.complete)
        assertTrue(result.missingRequiredClaims)
    }

    @Test
    fun `checkIfSignUpIsComplete - Complete`() = runTest {
        every { collectedClaimManager.areAllRequiredClaimCollected(any()) } returns true

        val result = manager.checkIfSignUpIsComplete(mockk(), mockk())

        assertTrue(result.complete)
    }
}
