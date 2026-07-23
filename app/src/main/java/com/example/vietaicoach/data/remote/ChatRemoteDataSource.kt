package com.example.vietaicoach.data.remote

import com.example.vietaicoach.data.remote.model.ChatRequest
import javax.inject.Inject

interface ChatRemoteDataSource {
    suspend fun submitMessage(prompt: String): Result<String>
}

class ChatRemoteDataSourceImpl @Inject constructor(
    private val service: ChatService
) : ChatRemoteDataSource {
    override suspend fun submitMessage(prompt: String): Result<String> {
        return runCatching { service.submitMessage(ChatRequest(prompt)).response }
    }
}