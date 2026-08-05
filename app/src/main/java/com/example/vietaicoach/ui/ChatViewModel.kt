package com.example.vietaicoach.ui

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.vietaicoach.data.ChatRepository
import com.example.vietaicoach.data.local.model.ChatMessageEntity
import com.example.vietaicoach.di.IOCoroutineDispatcher
import com.example.vietaicoach.ui.model.ChatUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository,
    @IOCoroutineDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    val promptState: TextFieldState = TextFieldState()

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    val messages: Flow<PagingData<ChatMessageEntity>> =
        repository.getMessages().cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            // Only the very first paint shows the skeleton; cached history paints immediately.
            val isEmpty = withContext(ioDispatcher) { repository.hasNoMessages() }
            _uiState.update { it.copy(isInitialLoading = isEmpty) }
        }
    }

    /** Called once the paging source reports its first page, clearing the skeleton. */
    fun onMessagesLoaded() {
        _uiState.update { it.copy(isInitialLoading = false) }
    }

    fun submitMessage(message: String) {
        if (message.isBlank()) return
        // Cleared up front: the message is already on screen as its own bubble, so leaving it in
        // the composer would show it twice.
        promptState.clearText()
        dispatch { repository.submitMessage(message) }
    }

    fun retryMessage(messageId: Long) {
        dispatch { repository.retryMessage(messageId) }
    }

    /**
     * A failure needs no state of its own — the repository has already marked the message
     * FAILED, and the list renders that. All this has to do is stop the typing indicator.
     */
    private fun dispatch(block: suspend () -> Result<String>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAwaitingReply = true) }

            withContext(ioDispatcher) { block() }

            _uiState.update { it.copy(isInitialLoading = false, isAwaitingReply = false) }
        }
    }
}
