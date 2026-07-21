package com.example.vietaicoach.ui.model

data class ResponseState(
    val response: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
