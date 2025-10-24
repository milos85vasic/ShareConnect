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
        mockAuthService = mockk()
        mockAuthState = MutableStateFlow(PlexAuthService.AuthState.Idle)
        every { mockAuthService.authState } returns mockAuthState
        every { mockAuthService.isAuthenticating() } returns false
        every { mockAuthService.startAuthentication() } just Runs
        every { mockAuthService.cancelAuthentication() } just Runs
        every { mockAuthService.reset() } just Runs

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