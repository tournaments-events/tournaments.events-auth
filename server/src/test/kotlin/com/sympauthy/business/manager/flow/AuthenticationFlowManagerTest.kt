package com.sympauthy.business.manager.flow

import com.sympauthy.business.manager.user.CollectedClaimManager
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
class AuthenticationFlowManagerTest {

    @MockK
    lateinit var collectedClaimManager: CollectedClaimManager

    @InjectMockKs
    lateinit var manager: AuthenticationFlowManager

    @Test
    fun `checkIfSignUpIsComplete - Non complete if missing claims`() = runTest {
        every { collectedClaimManager.areAllRequiredClaimCollected(any()) } returns false

        val result = manager.checkIfAuthenticationIsComplete(mockk(), mockk())

        assertFalse(result.complete)
        assertTrue(result.missingRequiredClaims)
    }

    @Test
    fun `checkIfSignUpIsComplete - Complete`() = runTest {
        every { collectedClaimManager.areAllRequiredClaimCollected(any()) } returns true

        val result = manager.checkIfAuthenticationIsComplete(mockk(), mockk())

        assertTrue(result.complete)
    }
}
