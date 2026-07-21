package com.example.vietaicoach.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vietaicoach.data.ChatRepository
import com.example.vietaicoach.data.ChatRepositoryImpl
import com.example.vietaicoach.ui.model.ResponseState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel(
    private val repository: ChatRepository = ChatRepositoryImpl(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {
    private val _promptStateFlow: MutableStateFlow<String> = MutableStateFlow("")
    val promptStateFlow: StateFlow<String> = _promptStateFlow.asStateFlow()

    private val _responseStateFlow: MutableStateFlow<ResponseState> = MutableStateFlow(ResponseState())
    val responseStateFlow: StateFlow<ResponseState> = _responseStateFlow.asStateFlow()

    fun updatePrompt(prompt: String) {
        _promptStateFlow.value = prompt
    }

    fun submitMessage(message: String) {
        viewModelScope.launch {
            _responseStateFlow.value = responseStateFlow.value.copy(isLoading = true)

            val result = withContext(ioDispatcher) {
                repository.submitMessage(message)
            }

            result.fold(
                onSuccess = { message ->
                    _responseStateFlow.value = ResponseState(
                        response = message,
                        isLoading = false,
                        errorMessage = null
                    )
                },
                onFailure = { error ->
                    Log.e(TAG, "Error: $error")

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