package com.sympauthy.business.manager.validationcode

import com.sympauthy.business.manager.util.assertThrowsLocalizedException
import com.sympauthy.business.mapper.ValidationCodeMapper
import com.sympauthy.business.model.code.ValidationCode
import com.sympauthy.business.model.code.ValidationCodeMedia.EMAIL
import com.sympauthy.business.model.code.ValidationCodeReason.EMAIL_CLAIM
import com.sympauthy.business.model.code.ValidationCodeReason.PHONE_NUMBER_CLAIM
import com.sympauthy.business.model.oauth2.AuthorizeAttempt
import com.sympauthy.business.model.user.CollectedClaim
import com.sympauthy.business.model.user.User
import com.sympauthy.business.model.user.claim.Claim
import com.sympauthy.data.model.ValidationCodeEntity
import com.sympauthy.data.repository.ValidationCodeRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class ValidationCodeManagerTest {

    @MockK
    lateinit var validationCodeGenerator: ValidationCodeGenerator

    @MockK
    lateinit var validationCodeRepository: ValidationCodeRepository

    val senders: MutableList<ValidationCodeMediaSender> = mutableListOf()

    @MockK
    lateinit var validationCodeMapper: ValidationCodeMapper

    @SpyK
    @InjectMockKs
    lateinit var manager: ValidationCodeManager

    @BeforeEach
    fun setUp() {
        senders.clear()
    }

    @Test
    fun `canSendValidationCodeForReason - Can send if a sender is available for the reason`() {
        val sender = mockk<ValidationCodeMediaSender> {
            every { media } returns EMAIL
        }
        senders.add(sender)

        assertTrue(manager.canSendValidationCodeForReason(EMAIL_CLAIM))
    }

    @Test
    fun `canSendValidationCodeForReason - Cannot send if no sender is available for the reason`() {
        assertFalse(manager.canSendValidationCodeForReason(EMAIL_CLAIM))
    }

    @Test
    fun `getSenderByMediaMap - Return corresponding sender for media`() {
        val sender = mockk<ValidationCodeMediaSender> {
            every { media } returns EMAIL
        }
        val emailClaim = mockk<Claim> {
            every { id } returns EMAIL.claim
        }
        val collectedEmailClaim = mockk<CollectedClaim> {
            every { claim } returns emailClaim
        }

        senders.add(sender)

        val result = manager.getSenderByMediaMap(
            medias = setOf(EMAIL),
            collectedClaims = listOf(collectedEmailClaim)
        )

        val resultSender = result[EMAIL]
        assertNotNull(resultSender)
        assertSame(EMAIL, resultSender?.media)
        assertSame(collectedEmailClaim, resultSender?.collectedClaim)
        assertSame(sender, resultSender?.sender)
    }

    @Test
    fun `queueRequiredValidationCodes - Generate codes for each reasons and send them through associated medias`() =
        runTest {
            val reason = EMAIL_CLAIM
            val user = mockk<User>()
            val authorizeAttempt = mockk<AuthorizeAttempt>()
            val collectedClaim = mockk<CollectedClaim>()
            val sender = mockk<ValidationCodeMediaSender> {
                every { media } returns EMAIL
            }
            val senderClaimTuple = ValidationCodeManager.SenderClaimTuple(
                media = EMAIL,
                collectedClaim = collectedClaim,
                sender = sender
            )
            val validationCode = mockk<ValidationCode> {
                every { media } returns EMAIL
            }

            every {
                manager.getSenderByMediaMap(
                    medias = setOf(EMAIL),
                    collectedClaims = listOf(collectedClaim)
                )
            } returns mapOf(EMAIL to senderClaimTuple)
            coEvery {
                validationCodeGenerator.generateValidationCode(
                    user = user,
                    authorizeAttempt = authorizeAttempt,
                    media = EMAIL,
                    reasons = listOf(reason)
                )
            } returns validationCode
            coEvery {
                sender.sendValidationCode(
                    user = user,
                    collectedClaim = collectedClaim,
                    validationCode = validationCode
                )
            } returns Unit

            val result = manager.queueRequiredValidationCodes(
                user = user,
                authorizeAttempt = authorizeAttempt,
                collectedClaims = listOf(collectedClaim),
                reasons = listOf(reason)
            )

            assertEquals(1, result.size)
            assertTrue(result.contains(validationCode))
        }

    @Test
    fun `getSenderByMediaMap - Throws exception if sender is missing`() {
        val collectedEmailClaim = mockk<CollectedClaim> {}

        assertThrowsLocalizedException("validationcode.missing_sender") {
            manager.getSenderByMediaMap(
                medias = setOf(EMAIL),
                collectedClaims = listOf(collectedEmailClaim)
            )
        }
    }

    @Test
    fun `getSenderByMediaMap - Throws exception if collected claim is missing`() {
        val sender = mockk<ValidationCodeMediaSender> {
            every { media } returns EMAIL
        }

        senders.add(sender)

        assertThrowsLocalizedException("validationcode.missing_claim") {
            manager.getSenderByMediaMap(
                medias = setOf(EMAIL),
                collectedClaims = listOf()
            )
        }
    }

    @Test
    fun `findCodeForReasonsDuringAttempt - Does not return non-matching reasons or expired`() = runTest {
        val authorizeAttemptId = UUID.randomUUID()
        val authorizeAttempt = mockk<AuthorizeAttempt> {
            every { id } returns authorizeAttemptId
        }
        val expiredEntity = mockk<ValidationCodeEntity>()
        val nonMatchingEntity = mockk<ValidationCodeEntity>()
        val matchingEntity = mockk<ValidationCodeEntity>()
        val expiredCode = mockk<ValidationCode> {
            every { reasons } returns listOf(EMAIL_CLAIM)
            every { expired } returns true
        }
        val matchingCode = mockk<ValidationCode> {
            every { reasons } returns listOf(EMAIL_CLAIM)
            every { expired } returns false
        }
        val nonMatchingCode = mockk<ValidationCode> {
            every { reasons } returns listOf(PHONE_NUMBER_CLAIM)
            every { expired } returns false
        }

        coEvery { validationCodeRepository.findByAttemptId(authorizeAttemptId) } returns listOf(
            expiredEntity, nonMatchingEntity, matchingEntity
        )
        every { validationCodeMapper.toValidationCode(expiredEntity) } returns expiredCode
        every { validationCodeMapper.toValidationCode(matchingEntity) } returns matchingCode
        every { validationCodeMapper.toValidationCode(nonMatchingEntity) } returns nonMatchingCode

        val result = manager.findCodeForReasonsDuringAttempt(
            authorizeAttempt = authorizeAttempt,
            reasons = listOf(EMAIL_CLAIM),
            includesExpired = false
        )

        assertEquals(1, result.size)
        assertSame(matchingCode, result.getOrNull(0))
    }

    @Test
    fun `findCodeForReasonsDuringAttempt - Does not return non-matching reasons`() = runTest {
        val authorizeAttemptId = UUID.randomUUID()
        val authorizeAttempt = mockk<AuthorizeAttempt> {
            every { id } returns authorizeAttemptId
        }
        val expiredEntity = mockk<ValidationCodeEntity>()
        val nonMatchingEntity = mockk<ValidationCodeEntity>()
        val matchingEntity = mockk<ValidationCodeEntity>()
        val expiredCode = mockk<ValidationCode> {
            every { reasons } returns listOf(EMAIL_CLAIM)
            every { expired } returns true
        }
        val matchingCode = mockk<ValidationCode> {
            every { reasons } returns listOf(EMAIL_CLAIM)
            every { expired } returns false
        }
        val nonMatchingCode = mockk<ValidationCode> {
            every { reasons } returns listOf(PHONE_NUMBER_CLAIM)
            every { expired } returns false
        }

        coEvery { validationCodeRepository.findByAttemptId(authorizeAttemptId) } returns listOf(
            expiredEntity, nonMatchingEntity, matchingEntity
        )
        every { validationCodeMapper.toValidationCode(expiredEntity) } returns expiredCode
        every { validationCodeMapper.toValidationCode(matchingEntity) } returns matchingCode
        every { validationCodeMapper.toValidationCode(nonMatchingEntity) } returns nonMatchingCode

        val result = manager.findCodeForReasonsDuringAttempt(
            authorizeAttempt = authorizeAttempt,
            reasons = listOf(EMAIL_CLAIM),
            includesExpired = true
        )

        assertEquals(2, result.size)
        assertSame(expiredCode, result.getOrNull(0))
        assertSame(matchingCode, result.getOrNull(1))
    }
}
