package com.example.vietaicoach.ui

import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.paging.PagingData
import com.example.vietaicoach.data.ChatRepository
import com.example.vietaicoach.data.local.model.ChatMessageEntity
import io.mockk.coEvery
import io.mockk.coVerify
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    private val repository: ChatRepository = mockk()
    private val ioDispatcher: CoroutineDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(ioDispatcher)
        every { repository.getMessages() } returns flowOf(PagingData.empty<ChatMessageEntity>())
        coEvery { repository.hasNoMessages() } returns false
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel() = ChatViewModel(repository, ioDispatcher)

    @Test
    fun `starts in the loading state when there is no local history`() = runTest(ioDispatcher) {
        coEvery { repository.hasNoMessages() } returns true

        val viewModel = viewModel()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isInitialLoading)
    }

    @Test
    fun `skips the skeleton when local history is already cached`() = runTest(ioDispatcher) {
        coEvery { repository.hasNoMessages() } returns false

        val viewModel = viewModel()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isInitialLoading)
    }

    @Test
    fun `onMessagesLoaded clears the skeleton`() = runTest(ioDispatcher) {
        coEvery { repository.hasNoMessages() } returns true
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onMessagesLoaded()

        assertFalse(viewModel.uiState.value.isInitialLoading)
    }

    @Test
    fun `submitMessage awaits a reply and settles once the repository responds`() =
        runTest(ioDispatcher) {
            coEvery { repository.submitMessage(any()) } returns Result.success("Success response")
            val viewModel = viewModel()

            viewModel.submitMessage("Test message")
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isAwaitingReply)
            coVerify { repository.submitMessage("Test message") }
        }

    @Test
    fun `submitMessage stops awaiting a reply when the send fails`() = runTest(ioDispatcher) {
        coEvery { repository.submitMessage(any()) } returns Result.failure(Throwable())
        val viewModel = viewModel()

        viewModel.submitMessage("Error message")
        advanceUntilIdle()

        // Failure is rendered inline on the message itself, so there is no screen-level
        // error state to assert — only that the typing indicator stops.
        assertFalse(viewModel.uiState.value.isAwaitingReply)
    }

    @Test
    fun `submitMessage clears the prompt so the text is not shown twice`() = runTest(ioDispatcher) {
        coEvery { repository.submitMessage(any()) } returns Result.success("Success response")
        val viewModel = viewModel()
        viewModel.promptState.setTextAndPlaceCursorAtEnd("Test message")

        viewModel.submitMessage("Test message")
        advanceUntilIdle()

        assertTrue(viewModel.promptState.text.isEmpty())
    }

    @Test
    fun `submitMessage clears the prompt even when the send fails`() = runTest(ioDispatcher) {
        coEvery { repository.submitMessage(any()) } returns Result.failure(Throwable())
        val viewModel = viewModel()
        viewModel.promptState.setTextAndPlaceCursorAtEnd("Error message")

        viewModel.submitMessage("Error message")
        advanceUntilIdle()

        // The message is already on screen as a FAILED bubble with its own Retry affordance,
        // so leaving the text in the composer would duplicate it.
        assertTrue(viewModel.promptState.text.isEmpty())
    }

    @Test
    fun `submitMessage ignores a blank prompt`() = runTest(ioDispatcher) {
        val viewModel = viewModel()

        viewModel.submitMessage("   ")
        advanceUntilIdle()

        coVerify(exactly = 0) { repository.submitMessage(any()) }
    }

    @Test
    fun `retryMessage delegates to the repository`() = runTest(ioDispatcher) {
        coEvery { repository.retryMessage(42L) } returns Result.success("Success response")
        val viewModel = viewModel()

        viewModel.retryMessage(42L)
        advanceUntilIdle()

        coVerify { repository.retryMessage(42L) }
        assertFalse(viewModel.uiState.value.isAwaitingReply)
    }
}
