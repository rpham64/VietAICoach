package com.example.vietaicoach.data.remote

import com.example.vietaicoach.data.remote.ChatService.Companion.BASE_URL
import com.example.vietaicoach.data.remote.model.ChatRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

interface ChatRemoteDataSource {
    suspend fun submitMessage(message: String): Result<String>
}

class ChatRemoteDataSourceImpl(
    private val service: ChatService =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create<ChatService>()
) : ChatRemoteDataSource {
    override suspend fun submitMessage(message: String): Result<String> {
        return runCatching { service.submitMessage(ChatRequest(message)).response }
    }
}