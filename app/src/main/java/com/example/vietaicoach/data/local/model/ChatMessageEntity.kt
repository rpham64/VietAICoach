package com.example.vietaicoach.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Delivery state of a locally-stored message. User messages are written optimistically
 * as [SENDING] and settle to [SENT] or [FAILED]; assistant messages are always [SENT].
 */
enum class DeliveryStatus { SENDING, SENT, FAILED }

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val role: ChatRole,
    val content: String,
    val timestamp: Long,
    val deliveryStatus: DeliveryStatus = DeliveryStatus.SENT,

    /** English gloss / pronunciation hint shown under an assistant bubble. */
    val romanization: String? = null,

    /** Inline correction: the learner's phrase as written, struck through in the UI. */
    val correctionOriginal: String? = null,

    /** Inline correction: the fixed phrase, emphasised in the UI. */
    val correctionFixed: String? = null,

    /** One-line reason the correction applies. */
    val correctionExplanation: String? = null,

    /** True when the coach confirmed the learner's sentence was already correct. */
    val isPraise: Boolean = false
)