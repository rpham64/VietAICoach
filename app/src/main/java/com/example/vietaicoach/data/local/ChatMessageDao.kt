package com.example.vietaicoach.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.vietaicoach.data.local.model.ChatMessageEntity
import com.example.vietaicoach.data.local.model.DeliveryStatus

@Dao
interface ChatMessageDao {
    @Insert
    suspend fun insert(message: ChatMessageEntity): Long

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC")
    fun getMessagesPaged(): PagingSource<Int, ChatMessageEntity>

    @Query("SELECT COUNT(*) FROM chat_messages")
    suspend fun count(): Int

    @Query("SELECT * FROM chat_messages WHERE id = :id")
    suspend fun getById(id: Long): ChatMessageEntity?

    @Query("UPDATE chat_messages SET deliveryStatus = :status WHERE id = :id")
    suspend fun updateDeliveryStatus(id: Long, status: DeliveryStatus)

    @Query("DELETE FROM chat_messages")
    suspend fun clearAll()
}
