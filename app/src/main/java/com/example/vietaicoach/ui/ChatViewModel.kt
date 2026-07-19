package com.example.vietaicoach.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vietaicoach.data.ChatRepository
import com.example.vietaicoach.data.ChatRepositoryImpl
import com.example.vietaicoach.ui.model.UiState
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
    private val _uiStateFlow: MutableStateFlow<UiState> = MutableStateFlow(UiState())
    val uiStateFlow: StateFlow<UiState> = _uiStateFlow.asStateFlow()

    fun submitMessage(message: String) {
        viewModelScope.launch {
            val result = withContext(ioDispatcher) {
                repository.submitMessage(message)
            }

            result.fold(
                onSuccess = { message ->
                    _uiStateFlow.value = UiState(
                        response = message,
                        errorMessage = null
                    )
                },
                onFailure = { error ->
                    _uiStateFlow.value = UiState(
                        response = uiStateFlow.value.response,
                        errorMessage = error.message.toString()
                    )
                }
            )
        }
    }
}