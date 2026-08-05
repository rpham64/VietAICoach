package com.example.vietaicoach.data.remote

import com.example.vietaicoach.data.remote.model.ChatRequest
import com.example.vietaicoach.data.remote.model.ChatResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatService {
    @POST("/chat")
    suspend fun submitMessage(@Body request: ChatRequest): ChatResponse

    companion object {
        const val BASE_URL = "http://127.0.0.1:8000"
    }
}
