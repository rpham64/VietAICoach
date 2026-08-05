package com.example.vietaicoach.data.local

import androidx.paging.PagingSource
import com.example.vietaicoach.data.local.model.ChatMessageEntity
import com.example.vietaicoach.data.local.model.ChatRole
import com.example.vietaicoach.data.local.model.DeliveryStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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
    fun `saveUserMessage inserts a user entity with the given status and returns its row id`() =
        runTest {
            val entitySlot = slot<ChatMessageEntity>()
            coEvery { dao.insert(capture(entitySlot)) } returns 7L

            val id = dataSource.saveUserMessage("Hello", DeliveryStatus.SENDING)

            assertEquals(7L, id)
            assertEquals(ChatRole.USER, entitySlot.captured.role)
            assertEquals("Hello", entitySlot.captured.content)
            assertEquals(DeliveryStatus.SENDING, entitySlot.captured.deliveryStatus)
        }

    @Test
    fun `saveAssistantMessage inserts an assistant entity carrying its coaching annotations`() =
        runTest {
            val entitySlot = slot<ChatMessageEntity>()
            coEvery { dao.insert(capture(entitySlot)) } returns 2L

            dataSource.saveAssistantMessage(
                content = "Giỏi lắm!",
                romanization = "Well done!",
                correctionOriginal = "đi đến chợ",
                correctionFixed = "đã đi đến chợ",
                correctionExplanation = "Add đã to mark the past tense.",
                isPraise = true
            )

            with(entitySlot.captured) {
                assertEquals(ChatRole.ASSISTANT, role)
                assertEquals("Giỏi lắm!", content)
                assertEquals(DeliveryStatus.SENT, deliveryStatus)
                assertEquals("Well done!", romanization)
                assertEquals("đi đến chợ", correctionOriginal)
                assertEquals("đã đi đến chợ", correctionFixed)
                assertEquals("Add đã to mark the past tense.", correctionExplanation)
                assertTrue(isPraise)
            }
        }

    @Test
    fun `saveAssistantMessage defaults to a plain bubble when the backend sends no annotations`() =
        runTest {
            val entitySlot = slot<ChatMessageEntity>()
            coEvery { dao.insert(capture(entitySlot)) } returns 3L

            dataSource.saveAssistantMessage(content = "Xin chào")

            with(entitySlot.captured) {
                assertEquals(null, romanization)
                assertEquals(null, correctionFixed)
                assertFalse(isPraise)
            }
        }

    @Test
    fun `getMessage delegates to the dao`() = runTest {
        val message = ChatMessageEntity(id = 1L, role = ChatRole.USER, content = "Hi", timestamp = 1L)
        coEvery { dao.getById(1L) } returns message

        assertEquals(message, dataSource.getMessage(1L))
    }

    @Test
    fun `updateDeliveryStatus delegates to the dao`() = runTest {
        coEvery { dao.updateDeliveryStatus(1L, DeliveryStatus.FAILED) } returns Unit

        dataSource.updateDeliveryStatus(1L, DeliveryStatus.FAILED)

        coVerify { dao.updateDeliveryStatus(1L, DeliveryStatus.FAILED) }
    }

    @Test
    fun `isEmpty is true only when the table holds no rows`() = runTest {
        coEvery { dao.count() } returns 0
        assertTrue(dataSource.isEmpty())

        coEvery { dao.count() } returns 4
        assertFalse(dataSource.isEmpty())
    }

    @Test
    fun `clearMessages delegates to the dao clearAll`() = runTest {
        coEvery { dao.clearAll() } returns Unit
        dataSource.clearMessages()
        coVerify { dao.clearAll() }
    }
}
