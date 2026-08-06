package com.example.vietaicoach.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.testing.asSnapshot
import com.example.vietaicoach.data.local.ChatLocalDataSource
import com.example.vietaicoach.data.local.model.ChatMessageEntity
import com.example.vietaicoach.data.local.model.ChatRole
import com.example.vietaicoach.data.local.model.DeliveryStatus
import com.example.vietaicoach.data.remote.ChatRemoteDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatRepositoryTest {

    private val remoteDataSource: ChatRemoteDataSource = mockk()
    private val localDataSource: ChatLocalDataSource = mockk()
    private val repository: ChatRepository = ChatRepositoryImpl(remoteDataSource, localDataSource)

    @Test
    fun `submitMessage saves the user message before calling the remote data source`() = runTest {
        coEvery { localDataSource.saveUserMessage(any(), any()) } returns 1L
        coEvery { localDataSource.saveAssistantMessage(any()) } returns 2L
        coEvery { remoteDataSource.submitMessage("Hello") } returns Result.success("Hi!")

        repository.submitMessage("Hello")

        coVerifyOrder {
            localDataSource.saveUserMessage("Hello", DeliveryStatus.SENT)
            remoteDataSource.submitMessage("Hello")
        }
    }

    @Test
    fun `submitMessage saves and returns the assistant response on success`() = runTest {
        coEvery { localDataSource.saveUserMessage(any(), any()) } returns 1L
        coEvery { localDataSource.saveAssistantMessage(any()) } returns 2L
        coEvery { remoteDataSource.submitMessage("Hello") } returns Result.success("Hi!")

        val result = repository.submitMessage("Hello")

        assertTrue(result.isSuccess)
        assertEquals("Hi!", result.getOrNull())
        coVerify { localDataSource.saveAssistantMessage("Hi!") }
    }

    @Test
    fun `submitMessage does not save an assistant message when the remote call fails`() = runTest {
        val error = RuntimeException("Network error")
        coEvery { localDataSource.saveUserMessage(any(), any()) } returns 1L
        coEvery { remoteDataSource.submitMessage("Hello") } returns Result.failure(error)

        val result = repository.submitMessage("Hello")

        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
        coVerify(exactly = 1) { localDataSource.saveUserMessage(any(), any()) }
        coVerify(exactly = 0) { localDataSource.saveAssistantMessage(any()) }
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
