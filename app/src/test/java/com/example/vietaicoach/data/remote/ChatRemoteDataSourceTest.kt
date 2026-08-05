package com.example.vietaicoach.data.remote

import android.util.Log
import com.example.vietaicoach.data.remote.model.ChatRequest
import com.example.vietaicoach.data.remote.model.ChatResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ChatRemoteDataSourceTest {

    private val service: ChatService = mockk()
    private val dataSource: ChatRemoteDataSource = ChatRemoteDataSourceImpl(service)

    /** Mirrors the configuration NetworkModule hands to the converter factory. */
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

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
        coEvery { service.submitMessage(any()) } returns ChatResponse(response = "Hello there")

        val result = dataSource.submitMessage("Hi")

        assertEquals("Hello there", result.getOrNull()?.response)
        coVerify { service.submitMessage(ChatRequest(prompt = "Hi")) }
    }

    @Test
    fun `submitMessage passes the coaching annotations through untouched`() = runTest {
        coEvery { service.submitMessage(any()) } returns ChatResponse(
            response = "Giỏi lắm!",
            romanization = "Well done!",
            correctionOriginal = "đi đến chợ",
            correctionFixed = "đã đi đến chợ",
            correctionExplanation = "Add đã to mark the past tense.",
            isCorrect = true
        )

        val reply = dataSource.submitMessage("Hi").getOrNull()!!

        assertEquals("Well done!", reply.romanization)
        assertEquals("đã đi đến chợ", reply.correctionFixed)
        assertTrue(reply.isCorrect)
    }

    @Test
    fun `submitMessage returns failure when the service throws`() = runTest {
        val exception = RuntimeException("Network error")
        coEvery { service.submitMessage(any()) } throws exception

        val result = dataSource.submitMessage("Hi")

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `ChatResponse decodes the minimal payload the current backend returns`() {
        val reply = json.decodeFromString<ChatResponse>("""{"response":"Xin chào"}""")

        assertEquals("Xin chào", reply.response)
        assertNull(reply.romanization)
        assertNull(reply.correctionFixed)
        assertFalse(reply.isCorrect)
    }

    @Test
    fun `ChatResponse decodes the annotated payload using its snake_case field names`() {
        val reply = json.decodeFromString<ChatResponse>(
            """
            {
              "response": "Giỏi lắm!",
              "romanization": "Well done!",
              "correction_original": "đi đến chợ",
              "correction_fixed": "đã đi đến chợ",
              "correction_explanation": "Add đã to mark the past tense.",
              "is_correct": true
            }
            """.trimIndent()
        )

        assertEquals("Giỏi lắm!", reply.response)
        assertEquals("Well done!", reply.romanization)
        assertEquals("đi đến chợ", reply.correctionOriginal)
        assertEquals("đã đi đến chợ", reply.correctionFixed)
        assertEquals("Add đã to mark the past tense.", reply.correctionExplanation)
        assertTrue(reply.isCorrect)
    }

    @Test
    fun `ChatResponse ignores fields the client does not know about`() {
        val reply = json.decodeFromString<ChatResponse>(
            """{"response":"Xin chào","tone_score":0.8}"""
        )

        assertEquals("Xin chào", reply.response)
    }

    @Test
    fun `ChatRequest encodes the prompt under its wire name`() {
        assertEquals("""{"prompt":"Hi"}""", json.encodeToString(ChatRequest(prompt = "Hi")))
    }
}
