package com.example.vietaicoach.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.testing.asSnapshot
import com.example.vietaicoach.data.local.ChatLocalDataSource
import com.example.vietaicoach.data.local.model.ChatMessageEntity
import com.example.vietaicoach.data.local.model.ChatRole
import com.example.vietaicoach.data.local.model.DeliveryStatus
import com.example.vietaicoach.data.remote.ChatRemoteDataSource
import com.example.vietaicoach.data.remote.model.ChatResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatRepositoryTest {

    private val remoteDataSource: ChatRemoteDataSource = mockk()
    private val localDataSource: ChatLocalDataSource = mockk()
    private val repository: ChatRepository = ChatRepositoryImpl(remoteDataSource, localDataSource)

    private fun stubLocalWrites(userMessageId: Long = 1L) {
        coEvery { localDataSource.saveUserMessage(any(), any()) } returns userMessageId
        coEvery {
            localDataSource.saveAssistantMessage(any(), any(), any(), any(), any(), any())
        } returns 2L
        coEvery { localDataSource.updateDeliveryStatus(any(), any()) } returns Unit
    }

    @Test
    fun `submitMessage writes the user bubble as SENDING before calling the remote data source`() =
        runTest {
            stubLocalWrites()
            coEvery { remoteDataSource.submitMessage("Hello") } returns
                Result.success(ChatResponse(response = "Hi!"))

            repository.submitMessage("Hello")

            coVerifyOrder {
                localDataSource.saveUserMessage("Hello", DeliveryStatus.SENDING)
                remoteDataSource.submitMessage("Hello")
            }
        }

    @Test
    fun `submitMessage settles the user bubble to SENT and saves the reply on success`() = runTest {
        stubLocalWrites(userMessageId = 42L)
        coEvery { remoteDataSource.submitMessage("Hello") } returns
            Result.success(ChatResponse(response = "Hi!"))

        val result = repository.submitMessage("Hello")

        assertTrue(result.isSuccess)
        assertEquals("Hi!", result.getOrNull())
        coVerify { localDataSource.updateDeliveryStatus(42L, DeliveryStatus.SENT) }
        coVerify { localDataSource.saveAssistantMessage("Hi!", null, null, null, null, false) }
    }

    @Test
    fun `submitMessage persists the coaching annotations that came back with the reply`() = runTest {
        stubLocalWrites()
        coEvery { remoteDataSource.submitMessage("Hello") } returns Result.success(
            ChatResponse(
                response = "Giỏi lắm!",
                romanization = "Well done!",
                correctionOriginal = "đi đến chợ",
                correctionFixed = "đã đi đến chợ",
                correctionExplanation = "Add đã to mark the past tense.",
                isCorrect = true
            )
        )

        val result = repository.submitMessage("Hello")

        assertEquals("Giỏi lắm!", result.getOrNull())
        coVerify {
            localDataSource.saveAssistantMessage(
                "Giỏi lắm!",
                "Well done!",
                "đi đến chợ",
                "đã đi đến chợ",
                "Add đã to mark the past tense.",
                true
            )
        }
    }

    @Test
    fun `submitMessage marks the user bubble FAILED and saves no reply when the remote call fails`() =
        runTest {
            val error = RuntimeException("Network error")
            stubLocalWrites(userMessageId = 42L)
            coEvery { remoteDataSource.submitMessage("Hello") } returns Result.failure(error)

            val result = repository.submitMessage("Hello")

            assertTrue(result.isFailure)
            assertEquals(error, result.exceptionOrNull())
            coVerify { localDataSource.updateDeliveryStatus(42L, DeliveryStatus.FAILED) }
            coVerify(exactly = 0) {
                localDataSource.saveAssistantMessage(any(), any(), any(), any(), any(), any())
            }
        }

    @Test
    fun `retryMessage re-sends the stored content and settles the existing bubble`() = runTest {
        stubLocalWrites()
        coEvery { localDataSource.getMessage(42L) } returns ChatMessageEntity(
            id = 42L,
            role = ChatRole.USER,
            content = "Hôm qua tôi đã đi đến chợ.",
            timestamp = 1L,
            deliveryStatus = DeliveryStatus.FAILED
        )
        coEvery { remoteDataSource.submitMessage("Hôm qua tôi đã đi đến chợ.") } returns
            Result.success(ChatResponse(response = "Giỏi lắm!"))

        val result = repository.retryMessage(42L)

        assertEquals("Giỏi lắm!", result.getOrNull())
        // The bubble is reused, not duplicated.
        coVerify(exactly = 0) { localDataSource.saveUserMessage(any(), any()) }
        coVerifyOrder {
            localDataSource.updateDeliveryStatus(42L, DeliveryStatus.SENDING)
            remoteDataSource.submitMessage("Hôm qua tôi đã đi đến chợ.")
            localDataSource.updateDeliveryStatus(42L, DeliveryStatus.SENT)
        }
    }

    @Test
    fun `retryMessage returns the bubble to FAILED when the retry also fails`() = runTest {
        stubLocalWrites()
        coEvery { localDataSource.getMessage(42L) } returns ChatMessageEntity(
            id = 42L,
            role = ChatRole.USER,
            content = "Xin chào",
            timestamp = 1L,
            deliveryStatus = DeliveryStatus.FAILED
        )
        coEvery { remoteDataSource.submitMessage("Xin chào") } returns
            Result.failure(RuntimeException("Still offline"))

        val result = repository.retryMessage(42L)

        assertTrue(result.isFailure)
        coVerify { localDataSource.updateDeliveryStatus(42L, DeliveryStatus.FAILED) }
    }

    @Test
    fun `retryMessage fails without touching the network when the message is gone`() = runTest {
        coEvery { localDataSource.getMessage(404L) } returns null

        val result = repository.retryMessage(404L)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        coVerify(exactly = 0) { remoteDataSource.submitMessage(any()) }
    }

    @Test
    fun `hasNoMessages reflects whether local history is empty`() = runTest {
        coEvery { localDataSource.isEmpty() } returns true
        assertTrue(repository.hasNoMessages())

        coEvery { localDataSource.isEmpty() } returns false
        assertFalse(repository.hasNoMessages())
    }

    @Test
    fun `getMessages builds paging data from the local data source`() = runTest {
        val entity = ChatMessageEntity(id = 1, role = ChatRole.USER, content = "Hi", timestamp = 1L)
        every { localDataSource.getMessages() } returns FakePagingSource(listOf(entity))

        val snapshot = repository.getMessages().asSnapshot()

        assertEquals(listOf(entity), snapshot)
    }

    private class FakePagingSource(
        private val items: List<ChatMessageEntity>
    ) : PagingSource<Int, ChatMessageEntity>() {
        override fun getRefreshKey(state: PagingState<Int, ChatMessageEntity>): Int? = null

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ChatMessageEntity> {
            return LoadResult.Page(data = items, prevKey = null, nextKey = null)
        }
    }
}
