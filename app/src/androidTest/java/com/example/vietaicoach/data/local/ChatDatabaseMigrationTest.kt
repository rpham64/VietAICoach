package com.example.vietaicoach.data.local

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Verifies [ChatDatabase.MIGRATION_1_2] against the exported schemas in `app/schemas`.
 * The migration is additive, so the point of these tests is that existing rows survive
 * with sane defaults rather than being dropped.
 */
@RunWith(AndroidJUnit4::class)
class ChatDatabaseMigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        ChatDatabase::class.java
    )

    @Test
    fun migration1To2PreservesExistingRows() {
        helper.createDatabase(TEST_DB, 1).use { db ->
            db.execSQL(
                "INSERT INTO chat_messages (role, content, timestamp) VALUES ('USER', 'Xin chào', 1)"
            )
            db.execSQL(
                "INSERT INTO chat_messages (role, content, timestamp) VALUES ('ASSISTANT', 'Chào bạn', 2)"
            )
        }

        // Throws if the post-migration schema does not match the exported 2.json.
        val db = helper.runMigrationsAndValidate(TEST_DB, 2, true, ChatDatabase.MIGRATION_1_2)

        db.query("SELECT role, content FROM chat_messages ORDER BY timestamp").use { cursor ->
            assertEquals(2, cursor.count)
            assertTrue(cursor.moveToFirst())
            assertEquals("USER", cursor.getString(0))
            assertEquals("Xin chào", cursor.getString(1))
            assertTrue(cursor.moveToNext())
            assertEquals("ASSISTANT", cursor.getString(0))
            assertEquals("Chào bạn", cursor.getString(1))
        }
    }

    @Test
    fun migration1To2BackfillsPreExistingMessagesAsDelivered() {
        helper.createDatabase(TEST_DB, 1).use { db ->
            db.execSQL(
                "INSERT INTO chat_messages (role, content, timestamp) VALUES ('USER', 'Xin chào', 1)"
            )
        }

        val db = helper.runMigrationsAndValidate(TEST_DB, 2, true, ChatDatabase.MIGRATION_1_2)

        db.query(
            "SELECT deliveryStatus, isPraise, romanization, correctionFixed FROM chat_messages"
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            // History predates delivery tracking, so it must read as settled — not stuck SENDING.
            assertEquals("SENT", cursor.getString(0))
            assertEquals(0, cursor.getInt(1))
            assertNull(cursor.getString(2))
            assertNull(cursor.getString(3))
        }
    }

    @Test
    fun migration1To2SucceedsOnAnEmptyDatabase() {
        helper.createDatabase(TEST_DB, 1).close()

        val db = helper.runMigrationsAndValidate(TEST_DB, 2, true, ChatDatabase.MIGRATION_1_2)

        db.query("SELECT COUNT(*) FROM chat_messages").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(0, cursor.getInt(0))
        }
    }

    private companion object {
        const val TEST_DB = "migration-test-db"
    }
}
