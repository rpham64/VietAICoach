package com.example.vietaicoach.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.vietaicoach.data.local.model.ChatMessageEntity

@Dao
interface ChatMessageDao {
    @Insert
    suspend fun insert(message: ChatMessageEntity): Long

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC")
    fun getMessagesPaged(): PagingSource<Int, ChatMessageEntity>

    @Query("DELETE FROM chat_messages")
    suspend fun clearAll()
}