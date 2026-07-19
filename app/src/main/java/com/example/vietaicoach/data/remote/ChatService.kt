package com.example.vietaicoach.data.remote

import com.example.vietaicoach.data.remote.model.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatService {
    @POST("/chat")
    suspend fun submitMessage(@Body message: String): Response

    companion object {
        const val BASE_URL = "http://10.0.2.2:8000"
    }
}