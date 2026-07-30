package com.example.vietaicoach.data.local

import androidx.paging.PagingSource
import com.example.vietaicoach.data.local.model.ChatMessageEntity
import com.example.vietaicoach.data.local.model.ChatRole
import javax.inject.Inject

interface ChatLocalDataSource {
    fun getMessages(): PagingSource<Int, ChatMessageEntity>
    suspend fun saveMessage(role: ChatRole, content: String)
    suspend fun clearMessages()
}

class ChatLocalDataSourceImpl @Inject constructor(
    private val chatMessageDao: ChatMessageDao
) : ChatLocalDataSource {
    override fun getMessages(): PagingSource<Int, ChatMessageEntity> = chatMessageDao.getMessagesPaged()

    override suspend fun saveMessage(role: ChatRole, content: String) {
        chatMessageDao.insert(
            ChatMessageEntity(
                role = role,
                content = content,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    override suspend fun clearMessages() {
        chatMessageDao.clearAll()
    }
}