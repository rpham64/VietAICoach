package com.example.vietaicoach.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Backend reply. `response` is the only field the current Python service returns; the coaching
 * annotations are optional so the app renders plain bubbles until the backend starts emitting
 * them (see design frames 2b/2e for the annotated variant).
 */
@Serializable
data class ChatResponse(
    @SerialName("response") val response: String,
    @SerialName("romanization") val romanization: String? = null,
    @SerialName("correction_original") val correctionOriginal: String? = null,
    @SerialName("correction_fixed") val correctionFixed: String? = null,
    @SerialName("correction_explanation") val correctionExplanation: String? = null,
    @SerialName("is_correct") val isCorrect: Boolean = false
)
