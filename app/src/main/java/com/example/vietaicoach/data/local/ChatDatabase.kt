package com.example.vietaicoach.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.vietaicoach.data.local.model.ChatMessageEntity

@Database(
    entities = [ChatMessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun chatMessageDao(): ChatMessageDao
}