package com.example.vietaicoach.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.vietaicoach.data.local.model.ChatMessageEntity

@Database(
    entities = [ChatMessageEntity::class],
    version = 2,
    exportSchema = true
)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        /** v1 -> v2: delivery status + coaching fields (romanization, inline correction, praise). */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE chat_messages ADD COLUMN deliveryStatus TEXT NOT NULL DEFAULT 'SENT'"
                )
                db.execSQL("ALTER TABLE chat_messages ADD COLUMN romanization TEXT")
                db.execSQL("ALTER TABLE chat_messages ADD COLUMN correctionOriginal TEXT")
                db.execSQL("ALTER TABLE chat_messages ADD COLUMN correctionFixed TEXT")
                db.execSQL("ALTER TABLE chat_messages ADD COLUMN correctionExplanation TEXT")
                db.execSQL(
                    "ALTER TABLE chat_messages ADD COLUMN isPraise INTEGER NOT NULL DEFAULT 0"
                )
            }
        }
    }
}
