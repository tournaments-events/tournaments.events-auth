package com.sympauthy.business.manager.flow

import com.sympauthy.business.manager.auth.oauth2.AuthorizationCodeManager
import com.sympauthy.business.manager.auth.oauth2.AuthorizeManager
import com.sympauthy.business.model.oauth2.AuthorizeAttempt
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.net.URI

@ExtendWith(MockKExtension::class)
class WebAuthorizationFlowRedirectUriBuilderTest {

    @MockK
    lateinit var authorizeManager: AuthorizeManager

    @MockK
    lateinit var authorizationCodeManager: AuthorizationCodeManager

    @InjectMockKs
    lateinit var uriBuilder: WebAuthorizationFlowRedirectUriBuilder

    @Test
    fun `appendStateToUri - Encode state and add it as query param to uri`() = runTest {
        val uri = URI.create("https://www.example.com")
        val authorizeAttempt = mockk<AuthorizeAttempt>()
        val encodedState = "encodedState"

        coEvery { authorizeManager.encodeState(authorizeAttempt) } returns encodedState

        val result = uriBuilder.appendStateToUri(
            authorizeAttempt = authorizeAttempt,
            uri = uri
        )

        assertEquals("https://www.example.com?state=encodedState", result.toString())
    }
}
