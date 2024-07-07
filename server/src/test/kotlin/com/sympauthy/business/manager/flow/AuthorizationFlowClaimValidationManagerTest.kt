package com.sympauthy.business.manager.flow

import com.sympauthy.business.manager.ClaimManager
import com.sympauthy.business.manager.user.CollectedClaimManager
import com.sympauthy.business.manager.validationcode.ValidationCodeManager
import com.sympauthy.business.model.code.ValidationCode
import com.sympauthy.business.model.code.ValidationCodeReason.EMAIL_CLAIM
import com.sympauthy.business.model.code.ValidationCodeReason.PHONE_NUMBER_CLAIM
import com.sympauthy.business.model.oauth2.AuthorizeAttempt
import com.sympauthy.business.model.user.CollectedClaim
import com.sympauthy.business.model.user.User
import com.sympauthy.business.model.user.claim.Claim
import com.sympauthy.business.model.user.claim.OpenIdClaim
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class AuthorizationFlowClaimValidationManagerTest {

    @MockK
    lateinit var claimManager: ClaimManager

    @MockK
    lateinit var collectedClaimManager: CollectedClaimManager

    @MockK
    lateinit var validationCodeManager: ValidationCodeManager

    @SpyK
    @InjectMockKs
    lateinit var manager: AuthorizationFlowClaimValidationManager

    @Test
    fun `getUnfilteredValidationCodeReasons - Verify email`() {
        val emailClaim = mockk<Claim> {
            every { id } returns OpenIdClaim.EMAIL.id
        }
        val collectedClaim = mockk<CollectedClaim> {
            every { claim } returns emailClaim
            every { verified } returns false
        }

        every { manager.validationCodeReasons } returns listOf(EMAIL_CLAIM)
        every { manager.getClaimValidatedBy(EMAIL_CLAIM) } returns emailClaim

        val result = manager.getUnfilteredValidationCodeReasons(listOf(collectedClaim))

        assertTrue(result.contains(EMAIL_CLAIM))
    }

    @Test
    fun `getUnfilteredValidationCodeReasons - Do not verify email if claim already verified`() {
        val emailClaim = mockk<Claim> {
            every { id } returns OpenIdClaim.EMAIL.id
        }
        val collectedClaim = mockk<CollectedClaim> {
            every { claim } returns emailClaim
            every { verified } returns true
        }

        every { manager.validationCodeReasons } returns listOf(EMAIL_CLAIM)
        every { manager.getClaimValidatedBy(EMAIL_CLAIM) } returns emailClaim

        val result = manager.getUnfilteredValidationCodeReasons(listOf(collectedClaim))

        assertFalse(result.contains(EMAIL_CLAIM))
    }

    @Test
    fun `getOrSendValidationCodes - Return existing codes if no additional reasons`() = runTest {
        val user = mockk<User>()
        val authorizeAttempt = mockk<AuthorizeAttempt>()
        val collectedClaims = listOf(mockk<CollectedClaim>())
        val existingValidationCode = mockk<ValidationCode> {
            every { reasons } returns listOf(EMAIL_CLAIM)
        }

        coEvery { collectedClaimManager.findClaimsReadableByAttempt(authorizeAttempt) } returns collectedClaims
        every { manager.getRequiredValidationCodeReasons(collectedClaims) } returns listOf(EMAIL_CLAIM)
        coEvery {
            validationCodeManager.findCodeForReasonsDuringAttempt(
                authorizeAttempt = authorizeAttempt,
                reasons = listOf(EMAIL_CLAIM)
            )
        } returns listOf(existingValidationCode)

        val result = manager.getOrSendValidationCodes(
            authorizeAttempt = authorizeAttempt,
            user = user,
        )

        assertEquals(1, result.count())
        assertEquals(existingValidationCode, result.getOrNull(0))
    }

    @Test
    fun `getOrSendValidationCodes - Revoke existing codes and send validation codes for reasons`() = runTest {
        val user = mockk<User>()
        val authorizeAttempt = mockk<AuthorizeAttempt>()
        val collectedClaims = listOf(mockk<CollectedClaim>())
        val existingValidationCode = mockk<ValidationCode> {
            every { reasons } returns listOf(EMAIL_CLAIM)
        }
        val sentEmailValidationCode = mockk<ValidationCode> {
            every { reasons } returns listOf(EMAIL_CLAIM)
        }
        val sentPhoneNumberValidationCode = mockk<ValidationCode> {
            every { reasons } returns listOf(PHONE_NUMBER_CLAIM)
        }

        coEvery { collectedClaimManager.findClaimsReadableByAttempt(authorizeAttempt) } returns collectedClaims
        every {
            manager.getRequiredValidationCodeReasons(collectedClaims)
        } returns listOf(EMAIL_CLAIM, PHONE_NUMBER_CLAIM)
        coEvery {
            validationCodeManager.findCodeForReasonsDuringAttempt(
                authorizeAttempt = authorizeAttempt,
                reasons = listOf(EMAIL_CLAIM, PHONE_NUMBER_CLAIM)
            )
        } returns listOf(existingValidationCode)
        coEvery { validationCodeManager.revokeValidationCodes(listOf(existingValidationCode)) } returns Unit
        coEvery {
            validationCodeManager.queueRequiredValidationCodes(
                user = user,
                authorizeAttempt = authorizeAttempt,
                collectedClaims = collectedClaims,
                reasons = listOf(EMAIL_CLAIM, PHONE_NUMBER_CLAIM)
            )
        } returns listOf(sentEmailValidationCode, sentPhoneNumberValidationCode)

        val result = manager.getOrSendValidationCodes(
            authorizeAttempt = authorizeAttempt,
            user = user,
        )

        assertEquals(2, result.count())
        assertEquals(sentEmailValidationCode, result.getOrNull(0))
        assertEquals(sentPhoneNumberValidationCode, result.getOrNull(1))
    }
}
