package com.example.vietaicoach.data.remote.model

import com.google.gson.annotations.SerializedName

data class ChatRequest(
    @SerializedName("prompt")
    val prompt: String
)