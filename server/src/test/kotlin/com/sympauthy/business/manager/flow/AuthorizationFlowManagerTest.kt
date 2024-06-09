package com.sympauthy.business.manager.flow

import com.sympauthy.business.manager.user.CollectedClaimManager
import com.sympauthy.config.model.AuthorizationFlowsConfig
import com.sympauthy.config.model.UrlsConfig
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class AuthorizationFlowManagerTest {

    @MockK
    lateinit var collectedClaimManager: CollectedClaimManager

    @MockK
    lateinit var claimValidationManager: AuthorizationFlowClaimValidationManager

    @MockK
    lateinit var authorizationFlowsConfig: AuthorizationFlowsConfig

    @MockK
    lateinit var uncheckedUrlsConfig: UrlsConfig

    @SpyK
    @InjectMockKs
    lateinit var manager: AuthorizationFlowManager

    @Test
    fun `checkIfAuthorizationIsComplete - Non complete if missing claims`() = runTest {
        every { collectedClaimManager.areAllRequiredClaimCollected(any()) } returns false
        every { claimValidationManager.getRequiredValidationCodeReasons(any()) } returns emptyList()

        val result = manager.checkIfAuthorizationIsComplete(mockk(), mockk())

        assertTrue(result.missingRequiredClaims)
    }

    @Test
    fun `checkIfAuthorizationIsComplete - Non complete if missing validation`() = runTest {
        every { collectedClaimManager.areAllRequiredClaimCollected(any()) } returns true
        every { claimValidationManager.getRequiredValidationCodeReasons(any()) } returns listOf(mockk())

        val result = manager.checkIfAuthorizationIsComplete(mockk(), mockk())

        assertTrue(result.missingValidation)
    }

    @Test
    fun `checkIfAuthorizationIsComplete - Complete`() = runTest {
        every { collectedClaimManager.areAllRequiredClaimCollected(any()) } returns true
        every { claimValidationManager.getRequiredValidationCodeReasons(any()) } returns emptyList()

        val result = manager.checkIfAuthorizationIsComplete(mockk(), mockk())

        assertTrue(result.complete)
    }
}
