package com.example.vietaicoach.ui.model

/**
 * The three states the final chat design specifies (frames 2a–2f): loading (skeleton),
 * success (annotated bubbles), error (failed delivery + retry).
 *
 * There is no screen-level error field on purpose. The design expresses failure inline on the
 * message that failed — via [com.example.vietaicoach.data.local.model.DeliveryStatus] — rather
 * than in a banner, so a failure is part of the message list, not of this state.
 */
data class ChatUiState(
    /** First-load skeleton — history has not been read from Room yet. */
    val isInitialLoading: Boolean = true,

    /** A reply is in flight; renders the three-dot typing bubble. */
    val isAwaitingReply: Boolean = false,

    /** Dialect shown in the header subtitle. */
    val dialect: String = "Southern"
)
