package com.example.vietaicoach.data.local

import androidx.paging.PagingSource
import com.example.vietaicoach.data.local.model.ChatMessageEntity
import com.example.vietaicoach.data.local.model.ChatRole
import com.example.vietaicoach.data.local.model.DeliveryStatus
import javax.inject.Inject

interface ChatLocalDataSource {
    fun getMessages(): PagingSource<Int, ChatMessageEntity>

    /** Writes a user message optimistically and returns its row id. */
    suspend fun saveUserMessage(content: String, status: DeliveryStatus): Long

    /** Writes an assistant reply along with any coaching annotations. */
    suspend fun saveAssistantMessage(
        content: String,
        romanization: String? = null,
        correctionOriginal: String? = null,
        correctionFixed: String? = null,
        correctionExplanation: String? = null,
        isPraise: Boolean = false
    ): Long

    suspend fun getMessage(id: Long): ChatMessageEntity?
    suspend fun updateDeliveryStatus(id: Long, status: DeliveryStatus)
    suspend fun isEmpty(): Boolean
    suspend fun clearMessages()
}

class ChatLocalDataSourceImpl @Inject constructor(
    private val chatMessageDao: ChatMessageDao
) : ChatLocalDataSource {

    override fun getMessages(): PagingSource<Int, ChatMessageEntity> =
        chatMessageDao.getMessagesPaged()

    override suspend fun saveUserMessage(content: String, status: DeliveryStatus): Long =
        chatMessageDao.insert(
            ChatMessageEntity(
                role = ChatRole.USER,
                content = content,
                timestamp = System.currentTimeMillis(),
                deliveryStatus = status
            )
        )

    override suspend fun saveAssistantMessage(
        content: String,
        romanization: String?,
        correctionOriginal: String?,
        correctionFixed: String?,
        correctionExplanation: String?,
        isPraise: Boolean
    ): Long = chatMessageDao.insert(
        ChatMessageEntity(
            role = ChatRole.ASSISTANT,
            content = content,
            timestamp = System.currentTimeMillis(),
            deliveryStatus = DeliveryStatus.SENT,
            romanization = romanization,
            correctionOriginal = correctionOriginal,
            correctionFixed = correctionFixed,
            correctionExplanation = correctionExplanation,
            isPraise = isPraise
        )
    )

    override suspend fun getMessage(id: Long): ChatMessageEntity? = chatMessageDao.getById(id)

    override suspend fun updateDeliveryStatus(id: Long, status: DeliveryStatus) =
        chatMessageDao.updateDeliveryStatus(id, status)

    override suspend fun isEmpty(): Boolean = chatMessageDao.count() == 0

    override suspend fun clearMessages() = chatMessageDao.clearAll()
}
