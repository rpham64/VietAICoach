package com.example.vietaicoach.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    @SerialName("prompt")
    val prompt: String
)
