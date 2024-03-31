package com.sympauthy.business.manager

import com.sympauthy.business.exception.BusinessException
import com.sympauthy.business.model.oauth2.Scope
import com.sympauthy.config.model.EnabledAuthConfig
import com.sympauthy.config.model.EnabledScopesConfig
import com.sympauthy.exception.LocalizedException
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ScopeManagerTest {

    @MockK
    lateinit var authConfig: EnabledAuthConfig

    @MockK
    lateinit var scopesConfig: EnabledScopesConfig

    @SpyK
    @InjectMockKs
    lateinit var scopeManager: ScopeManager

    @Test
    fun `parseRequestScope - Parse request scopes`() = runTest {
        val scopeOne = "scope/one"
        val foundScopeOne = mockk<Scope> {
            every { scope } returns scopeOne
        }
        coEvery { scopeManager.find(scopeOne) } returns foundScopeOne

        val scopeTwo = "scope/tow"
        val foundScopeTwo = mockk<Scope> {
            every { scope } returns scopeTwo
        }
        coEvery { scopeManager.find(scopeTwo) } returns foundScopeTwo

        val result = scopeManager.parseRequestScope("$scopeOne     $scopeTwo")

        assertEquals(2, result?.count())
        assertSame(foundScopeOne, result?.getOrNull(0))
        assertSame(foundScopeTwo, result?.getOrNull(1))
    }

    @Test
    fun `findOrThrow - Find scope`() = runTest {
        val scope = "scope"
        val foundScope = mockk<Scope>()
        coEvery { scopeManager.find(scope) } returns foundScope
        val result = scopeManager.findOrThrow(scope)
        assertSame(foundScope, result)
    }

    @Test
    fun `findOrThrow - Throw if scope cannot be found`() = runTest {
        val scope = "scope"
        coEvery { scopeManager.find(scope) } throws mockk<LocalizedException>()
        assertThrows<BusinessException> {
            scopeManager.findOrThrow(scope)
        }
    }
}
