package tournament.events.auth.business.manager.auth.oauth2

import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import tournament.events.auth.api.exception.OAuth2Exception
import tournament.events.auth.business.manager.jwt.JwtManager
import tournament.events.auth.business.mapper.AuthenticationTokenMapper
import tournament.events.auth.business.model.oauth2.AuthenticationToken
import tournament.events.auth.business.model.oauth2.EncodedAuthenticationToken
import tournament.events.auth.data.repository.AuthenticationTokenRepository
import tournament.events.auth.exception.localizedExceptionOf
import java.time.LocalDateTime.now

@ExtendWith(MockKExtension::class)
@MockKExtension.CheckUnnecessaryStub
class TokenManagerTest {

    @MockK
    lateinit var jwtManager: JwtManager

    @MockK
    lateinit var accessTokenGenerator: AccessTokenGenerator

    @MockK
    lateinit var refreshTokenGenerator: RefreshTokenGenerator

    @MockK
    lateinit var tokenRepository: AuthenticationTokenRepository

    @MockK
    lateinit var tokenMapper: AuthenticationTokenMapper

    @InjectMockKs
    lateinit var tokenManager: TokenManager

    @Test
    fun `shouldRefreshToken - False if refresh has no expiration`() {
        val refreshToken = mockk<AuthenticationToken>()
        every { refreshToken.expirationDate } returns null
        assertFalse(tokenManager.shouldRefreshToken(refreshToken, mockk()))
    }

    @Test
    fun `shouldRefreshToken - True if refresh expiration is before access expiration`() {
        val refreshToken = mockk<AuthenticationToken>()
        val accessToken = mockk<EncodedAuthenticationToken>()

        every { refreshToken.expirationDate } returns now().minusDays(1)
        every { accessToken.expirationDate } returns now()

        assertTrue(tokenManager.shouldRefreshToken(refreshToken, accessToken))
    }

    @Test
    fun `shouldRefreshToken - False if refresh expiration is after access expiration`() {
        val refreshToken = mockk<AuthenticationToken>()
        val accessToken = mockk<EncodedAuthenticationToken>()

        every { refreshToken.expirationDate } returns now()
        every { accessToken.expirationDate } returns now().minusDays(1)

        assertFalse(tokenManager.shouldRefreshToken(refreshToken, accessToken))
    }

    @Test
    fun `refreshToken - Throws OAuth2Exception if token is invalid`() {
        val exception = localizedExceptionOf("test")
        coEvery { jwtManager.decodeAndVerify(any(), any()) } throws exception

        assertThrows<OAuth2Exception> {
            runBlocking {
                tokenManager.refreshToken(mock(), "test")
            }
        }
    }
}
