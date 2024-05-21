package com.sympauthy.business.manager.flow

import com.sympauthy.business.manager.user.CollectedClaimManager
import com.sympauthy.business.manager.validationcode.ValidationCodeManager
import com.sympauthy.business.model.code.ValidationCode
import com.sympauthy.business.model.code.ValidationCodeReason
import com.sympauthy.business.model.oauth2.AuthorizeAttempt
import com.sympauthy.business.model.user.CollectedClaim
import com.sympauthy.business.model.user.User
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class AuthenticationFlowManagerTest {

    @MockK
    lateinit var collectedClaimManager: CollectedClaimManager

    @MockK
    lateinit var validationCodeManager: ValidationCodeManager

    @SpyK
    @InjectMockKs
    lateinit var manager: AuthenticationFlowManager

    @Test
    fun `queueRequiredValidationCodes - Queues validation codes`() = runTest {
        val user = mockk<User>()
        val authorizeAttempt = mockk<AuthorizeAttempt>()
        val reasons = listOf(
            ValidationCodeReason.EMAIL_CLAIM,
            ValidationCodeReason.RESET_PASSWORD
        )
        val collectedClaims = listOf(
            mockk<CollectedClaim>()
        )
        val validationCode = mockk<ValidationCode>()

        every { manager.getRequiredValidationCodeReasons(collectedClaims) } returns reasons
        coEvery {
            validationCodeManager.queueRequiredValidationCodes(user, authorizeAttempt, reasons)
        } returns listOf(validationCode)

        val result = manager.queueRequiredValidationCodes(
            user = user,
            authorizeAttempt = authorizeAttempt,
            collectedClaims = collectedClaims
        )

        assertEquals(1, result.count())
        assertEquals(validationCode, result.getOrNull(0))
    }

    @Test
    fun `checkIfSignUpIsComplete - Non complete if missing claims`() = runTest {
        every { collectedClaimManager.areAllRequiredClaimCollected(any()) } returns false
        every { manager.getRequiredValidationCodeReasons(any()) } returns emptyList()

        val result = manager.checkIfAuthenticationIsComplete(mockk(), mockk())

        assertTrue(result.missingRequiredClaims)
    }

    @Test
    fun `checkIfSignUpIsComplete - Non complete if missing validation`() = runTest {
        every { collectedClaimManager.areAllRequiredClaimCollected(any()) } returns true
        every { manager.getRequiredValidationCodeReasons(any()) } returns listOf(mockk())

        val result = manager.checkIfAuthenticationIsComplete(mockk(), mockk())

        assertTrue(result.missingValidation)
    }

    @Test
    fun `checkIfSignUpIsComplete - Complete`() = runTest {
        every { collectedClaimManager.areAllRequiredClaimCollected(any()) } returns true
        every { manager.getRequiredValidationCodeReasons(any()) } returns emptyList()

        val result = manager.checkIfAuthenticationIsComplete(mockk(), mockk())

        assertTrue(result.complete)
    }
}
