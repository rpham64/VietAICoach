package com.example.vietaicoach.data.local

import androidx.paging.PagingSource
import com.example.vietaicoach.data.local.model.ChatMessageEntity
import com.example.vietaicoach.data.local.model.ChatRole
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ChatLocalDataSourceTest {

    private val dao: ChatMessageDao = mockk()
    private val dataSource: ChatLocalDataSource = ChatLocalDataSourceImpl(dao)

    @Test
    fun `getMessages delegates to the dao paging source`() {
        val pagingSource: PagingSource<Int, ChatMessageEntity> = mockk()
        every { dao.getMessagesPaged() } returns pagingSource

        val result = dataSource.getMessages()

        assertEquals(pagingSource, result)
    }

    @Test
    fun `saveMessage inserts a chat message entity with the given role and content`() = runTest {
        val entitySlot = slot<ChatMessageEntity>()
        coEvery { dao.insert(capture(entitySlot)) } returns 1L

        dataSource.saveMessage(ChatRole.USER, "Hello")

        coVerify { dao.insert(any()) }
        assertEquals(ChatRole.USER, entitySlot.captured.role)
        assertEquals("Hello", entitySlot.captured.content)
    }

    @Test
    fun `clearMessages delegates to the dao clearAll`() = runTest {
        coEvery { dao.clearAll() } returns Unit
        dataSource.clearMessages()
        coVerify { dao.clearAll() }
    }
}
