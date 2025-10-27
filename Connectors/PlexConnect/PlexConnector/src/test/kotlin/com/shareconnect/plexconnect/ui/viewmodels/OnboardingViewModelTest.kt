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