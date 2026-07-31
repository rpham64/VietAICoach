package com.example.vietaicoach.data.local

import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.vietaicoach.data.local.model.ChatMessageEntity
import com.example.vietaicoach.data.local.model.ChatRole
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatDatabaseTest {

    private lateinit var database: ChatDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ChatDatabase::class.java
        ).build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun chatMessageDaoIsProvided() {
        assertNotNull(database.chatMessageDao())
    }

    @Test
    fun messagesInsertedThroughTheDaoArePersistedInTheDatabase() = runBlocking {
        val dao = database.chatMessageDao()
        dao.insert(ChatMessageEntity(role = ChatRole.USER, content = "Hello", timestamp = 1L))

        val result = dao.getMessagesPaged().load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false)
        ) as PagingSource.LoadResult.Page

        assertEquals("Hello", result.data.single().content)
    }
}
