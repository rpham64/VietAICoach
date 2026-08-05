package com.example.vietaicoach.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.vietaicoach.data.local.ChatLocalDataSource
import com.example.vietaicoach.data.local.model.ChatMessageEntity
import com.example.vietaicoach.data.local.model.DeliveryStatus
import com.example.vietaicoach.data.remote.ChatRemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ChatRepository {
    suspend fun submitMessage(message: String): Result<String>
    fun getMessages(): Flow<PagingData<ChatMessageEntity>>
}

class ChatRepositoryImpl @Inject constructor(
    private val remoteDataSource: ChatRemoteDataSource,
    private val localDataSource: ChatLocalDataSource
) : ChatRepository {
    override suspend fun submitMessage(message: String): Result<String> {
        localDataSource.saveUserMessage(message, DeliveryStatus.SENT)

        return remoteDataSource.submitMessage(message).onSuccess { response ->
            localDataSource.saveAssistantMessage(response)
        }
    }

    override fun getMessages(): Flow<PagingData<ChatMessageEntity>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            pagingSourceFactory = { localDataSource.getMessages() }
        ).flow
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}