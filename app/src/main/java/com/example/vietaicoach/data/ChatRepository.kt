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
    /** Sends a new learner message. Returns the assistant reply text on success. */
    suspend fun submitMessage(message: String): Result<String>

    /** Re-sends a message that previously landed in [DeliveryStatus.FAILED]. */
    suspend fun retryMessage(messageId: Long): Result<String>

    fun getMessages(): Flow<PagingData<ChatMessageEntity>>

    /** True when there is no local history yet — drives the first-load skeleton. */
    suspend fun hasNoMessages(): Boolean
}

class ChatRepositoryImpl @Inject constructor(
    private val remoteDataSource: ChatRemoteDataSource,
    private val localDataSource: ChatLocalDataSource
) : ChatRepository {

    override suspend fun submitMessage(message: String): Result<String> {
        val messageId = localDataSource.saveUserMessage(message, DeliveryStatus.SENDING)
        return send(messageId, message)
    }

    override suspend fun retryMessage(messageId: Long): Result<String> {
        val existing = localDataSource.getMessage(messageId)
            ?: return Result.failure(IllegalArgumentException("Message $messageId no longer exists"))

        localDataSource.updateDeliveryStatus(messageId, DeliveryStatus.SENDING)
        return send(messageId, existing.content)
    }

    /**
     * The bubble is already on screen as [DeliveryStatus.SENDING] by the time this runs, so the
     * only job here is to settle it — and, on success, append the coach's reply.
     */
    private suspend fun send(messageId: Long, content: String): Result<String> =
        remoteDataSource.submitMessage(content)
            .onSuccess { reply ->
                localDataSource.updateDeliveryStatus(messageId, DeliveryStatus.SENT)
                localDataSource.saveAssistantMessage(
                    content = reply.response,
                    romanization = reply.romanization,
                    correctionOriginal = reply.correctionOriginal,
                    correctionFixed = reply.correctionFixed,
                    correctionExplanation = reply.correctionExplanation,
                    isPraise = reply.isCorrect
                )
            }
            .onFailure {
                localDataSource.updateDeliveryStatus(messageId, DeliveryStatus.FAILED)
            }
            .map { it.response }

    override fun getMessages(): Flow<PagingData<ChatMessageEntity>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            pagingSourceFactory = { localDataSource.getMessages() }
        ).flow
    }

    override suspend fun hasNoMessages(): Boolean = localDataSource.isEmpty()

    companion object {
        private const val PAGE_SIZE = 20
    }
}
