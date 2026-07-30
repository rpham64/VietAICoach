package com.example.vietaicoach.ui

import androidx.paging.PagingData
import com.example.vietaicoach.data.ChatRepository
import com.example.vietaicoach.data.local.ChatMessageEntity
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    private val repository: ChatRepository = mockk<ChatRepository>()
    private val ioDispatcher: CoroutineDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: ChatViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(ioDispatcher)
        every { repository.getMessages() } returns flowOf(PagingData.empty<ChatMessageEntity>())
        viewModel = ChatViewModel(repository, ioDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `submitMessage submits message and gets success response`() = runTest(ioDispatcher) {
        coEvery { repository.submitMessage(any<String>()) } returns Result.success("Success response")

        viewModel.submitMessage("Test message")
        advanceUntilIdle()

        viewModel.responseStateFlow.value.let { successResult ->
            assert(successResult.response == "Success response")
            assertNull(successResult.errorMessage)
        }
    }

    @Test
    fun `submitMessage submits message and gets error response`() = runTest(ioDispatcher) {
        coEvery { repository.submitMessage(any<String>()) } returns Result.failure(Throwable())

        viewModel.submitMessage("Error message")
        advanceUntilIdle()

        viewModel.responseStateFlow.value.let { errorResponse ->
            assert(errorResponse.response.isEmpty())
            assertNotNull(errorResponse.errorMessage)
        }
    }
}