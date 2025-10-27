/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.shareconnect.plexconnect.ui.viewmodels

import com.shareconnect.plexconnect.service.PlexAuthService
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.Runs
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthenticationViewModelTest {

    private lateinit var viewModel: AuthenticationViewModel
    private lateinit var mockAuthService: PlexAuthService
    private lateinit var mockAuthState: MutableStateFlow<PlexAuthService.AuthState>
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockAuthService = mockk(relaxed = true)
        mockAuthState = MutableStateFlow(PlexAuthService.AuthState.Idle)
        every { mockAuthService.authState } returns mockAuthState
        every { mockAuthService.isAuthenticating() } returns false

        viewModel = AuthenticationViewModel(mockAuthService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `authState exposes auth service state`() = runTest {
        // Given
        val expectedState = PlexAuthService.AuthState.Authenticated("test_token")
        mockAuthState.value = expectedState

        // When
        val result = viewModel.authState.first()

        // Then
        assert(result == expectedState)
    }

    @Test
    fun `isAuthenticating starts as false`() = runTest {
        // When
        val result = viewModel.isAuthenticating.first()

        // Then
        assertFalse(result)
    }

    @Test
    fun `startAuthentication calls service when not already authenticating`() = runTest {
        // Given
        every { mockAuthService.isAuthenticating() } returns false

        // When
        viewModel.startAuthentication()
        advanceUntilIdle()

        // Then
        verify { mockAuthService.startAuthentication(any()) }
    }

    @Test
    fun `startAuthentication does nothing when already authenticating`() = runTest {
        // Given
        every { mockAuthService.isAuthenticating() } returns true

        // When
        viewModel.startAuthentication()

        // Then
        assertFalse(viewModel.isAuthenticating.value)
        verify(exactly = 0) { mockAuthService.startAuthentication() }
    }

    @Test
    fun `cancelAuthentication calls service cancel and resets isAuthenticating`() = runTest {
        // Given
        viewModel.startAuthentication() // Set to true

        // When
        viewModel.cancelAuthentication()

        // Then
        assertFalse(viewModel.isAuthenticating.value)
        verify { mockAuthService.cancelAuthentication() }
    }

    @Test
    fun `resetAuthentication calls service reset and resets isAuthenticating`() = runTest {
        // Given
        viewModel.startAuthentication() // Set to true

        // When
        viewModel.resetAuthentication()

        // Then
        assertFalse(viewModel.isAuthenticating.value)
        verify { mockAuthService.reset() }
    }


}