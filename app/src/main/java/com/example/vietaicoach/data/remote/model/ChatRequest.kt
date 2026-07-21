package com.example.vietaicoach.data.remote.model

import com.google.gson.annotations.SerializedName

data class ChatRequest(
    @SerializedName("message")
    val message: String
)