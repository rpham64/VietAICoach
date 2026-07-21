package com.example.vietaicoach.data

import com.example.vietaicoach.data.remote.ChatRemoteDataSource
import javax.inject.Inject

interface ChatRepository {
    suspend fun submitMessage(message: String): Result<String>
}

class ChatRepositoryImpl @Inject constructor(
    private val remoteDataSource: ChatRemoteDataSource
) : ChatRepository {
    override suspend fun submitMessage(message: String): Result<String> {
        return remoteDataSource.submitMessage(message)
    }
}