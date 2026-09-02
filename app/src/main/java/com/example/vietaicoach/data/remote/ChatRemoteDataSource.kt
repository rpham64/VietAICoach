package com.example.vietaicoach.data.remote

import android.util.Log
import com.example.vietaicoach.data.remote.model.ChatRequest
import com.example.vietaicoach.data.remote.model.ChatResponse
import javax.inject.Inject

interface ChatRemoteDataSource {
    suspend fun submitMessage(prompt: String): Result<ChatResponse>
}

class ChatRemoteDataSourceImpl @Inject constructor(
    private val service: ChatService
) : ChatRemoteDataSource {
    override suspend fun submitMessage(prompt: String): Result<ChatResponse> {
        return runCatching { service.submitMessage(ChatRequest(prompt)) }
            .onFailure { error -> Log.e(TAG, "Failed to submit message", error) }
    }

    companion object {
        private const val TAG = "ChatRemoteDataSource"
    }
}
