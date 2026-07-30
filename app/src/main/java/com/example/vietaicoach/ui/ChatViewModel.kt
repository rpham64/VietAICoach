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
import com.example.vietaicoach.ui.model.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository,
    @IOCoroutineDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    val promptState: TextFieldState = TextFieldState()

    private val _responseStateFlow: MutableStateFlow<ResponseState> = MutableStateFlow(ResponseState())
    val responseStateFlow: StateFlow<ResponseState> = _responseStateFlow.asStateFlow()

    val messages: Flow<PagingData<ChatMessageEntity>> = repository.getMessages().cachedIn(viewModelScope)

    fun submitMessage(message: String) {
        viewModelScope.launch {
            _responseStateFlow.value = responseStateFlow.value.copy(isLoading = true)

            val result = withContext(ioDispatcher) {
                repository.submitMessage(message)
            }

            result.fold(
                onSuccess = { message ->
                    promptState.clearText()
                    _responseStateFlow.value = ResponseState(
                        response = message,
                        isLoading = false,
                        errorMessage = null
                    )
                },
                onFailure = { error ->
                    _responseStateFlow.value = ResponseState(
                        response = responseStateFlow.value.response,
                        isLoading = false,
                        errorMessage = error.message.toString()
                    )
                }
            )
        }
    }

    companion object {
        private const val TAG = "ChatViewModel"
    }
}