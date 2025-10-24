package com.shareconnect.plexconnect.ui.viewmodels

import com.shareconnect.plexconnect.data.repository.PlexServerRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class OnboardingViewModelTest {

    private lateinit var viewModel: OnboardingViewModel
    private lateinit var mockRepository: PlexServerRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `hasServers returns true when server count is greater than 0`() = runTest {
        // Given
        coEvery { mockRepository.getServerCount() } returns 1
        viewModel = OnboardingViewModel(mockRepository)

        // When - ViewModel is initialized, it calls checkExistingServers

        // Then - advance time to let coroutine complete
        testScheduler.advanceUntilIdle()
        val result = viewModel.hasServers.value
        assertTrue(result)
    }

    @Test
    fun `hasServers returns false when server count is 0`() = runTest {
        // Given
        coEvery { mockRepository.getServerCount() } returns 0
        viewModel = OnboardingViewModel(mockRepository)

        // When - ViewModel is initialized, it calls checkExistingServers

        // Then
        testScheduler.advanceUntilIdle()
        val result = viewModel.hasServers.value
        assertFalse(result)
    }

    @Test
    fun `hasServers returns false when server count is negative`() = runTest {
        // Given
        coEvery { mockRepository.getServerCount() } returns -1
        viewModel = OnboardingViewModel(mockRepository)

        // When - ViewModel is initialized, it calls checkExistingServers

        // Then
        testScheduler.advanceUntilIdle()
        val result = viewModel.hasServers.value
        assertFalse(result)
    }
}