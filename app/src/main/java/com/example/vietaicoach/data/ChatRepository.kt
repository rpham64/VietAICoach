package com.example.vietaicoach.data

import com.example.vietaicoach.data.remote.ChatRemoteDataSource
import com.example.vietaicoach.data.remote.ChatRemoteDataSourceImpl

interface ChatRepository {
    suspend fun submitMessage(message: String): Result<String>
}

class ChatRepositoryImpl(
    private val remoteDataSource: ChatRemoteDataSource = ChatRemoteDataSourceImpl()
) : ChatRepository {
    override suspend fun submitMessage(message: String): Result<String> {
        return remoteDataSource.submitMessage(message)
    }
}