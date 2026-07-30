package com.example.vietaicoach.data.remote

import android.util.Log
import com.example.vietaicoach.data.remote.model.ChatRequest
import com.example.vietaicoach.data.remote.model.Response
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ChatRemoteDataSourceTest {

    private val service: ChatService = mockk()
    private val dataSource: ChatRemoteDataSource = ChatRemoteDataSourceImpl(service)

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `submitMessage returns the response body from the service`() = runTest {
        coEvery { service.submitMessage(any()) } returns Response(response = "Hello there")

        val result = dataSource.submitMessage("Hi")

        assertEquals("Hello there", result.getOrNull())
        coVerify { service.submitMessage(ChatRequest(prompt = "Hi")) }
    }

    @Test
    fun `submitMessage returns failure when the service throws`() = runTest {
        val exception = RuntimeException("Network error")
        coEvery { service.submitMessage(any()) } throws exception

        val result = dataSource.submitMessage("Hi")

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
