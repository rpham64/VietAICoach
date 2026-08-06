package com.example.vietaicoach.ui

import android.content.ClipData
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.vietaicoach.data.local.model.ChatMessageEntity
import com.example.vietaicoach.data.local.model.ChatRole
import com.example.vietaicoach.data.local.model.DeliveryStatus
import com.example.vietaicoach.ui.model.ChatUiState
import com.example.vietaicoach.ui.theme.LocalCoachColors
import com.example.vietaicoach.ui.theme.VietAICoachTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

/**
 * The 4dp corner *is* the bubble's tail — the design draws no separate pointer, it just
 * flattens the corner nearest the speaker.
 */
private val BubbleRadius = 16.dp
private val BubbleTail = 4.dp
private val AssistantBubbleShape =
    RoundedCornerShape(BubbleRadius, BubbleRadius, BubbleRadius, BubbleTail)
private val UserBubbleShape =
    RoundedCornerShape(BubbleRadius, BubbleRadius, BubbleTail, BubbleRadius)

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val messages = viewModel.messages.collectAsLazyPagingItems()

    ChatScreen(
        promptState = viewModel.promptState,
        uiState = uiState,
        messages = messages,
        onSubmitMessage = { viewModel.submitMessage(viewModel.promptState.text.toString()) },
        modifier = modifier
    )
}

@Composable
fun ChatScreen(
    promptState: TextFieldState,
    uiState: ChatUiState,
    messages: LazyPagingItems<ChatMessageEntity>,
    onSubmitMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ChatHeader(dialect = uiState.dialect)

        MessageList(
            messages = messages,
            isAssistantTyping = uiState.isAwaitingReply,
            listState = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        MessageComposer(
            promptState = promptState,
            onSubmitMessage = {
                onSubmitMessage()
                coroutineScope.launch { listState.animateScrollToItem(0) }
            }
        )
    }
}

/* ------------------------------------------------------------------ header */

/**
 * Deliberately not an M3 [androidx.compose.material3.TopAppBar] — the mockup renders the frame
 * without one and supplies this custom row in its place.
 */
@Composable
private fun ChatHeader(
    dialect: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "C",
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Chào Bạn",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Practicing · $dialect dialect",
                fontSize = 11.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/* ------------------------------------------------------------------ list */

@Composable
fun MessageList(
    messages: LazyPagingItems<ChatMessageEntity>,
    isAssistantTyping: Boolean,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        state = listState,
        modifier = modifier.testTag("MessageList"),
        reverseLayout = true,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (isAssistantTyping) {
            item(key = "typing-indicator") {
                TypingIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
        items(
            count = messages.itemCount,
            key = messages.itemKey { it.id }
        ) { index ->
            messages[index]?.let { message ->
                MessageBubble(message = message, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

/**
 * The design caps a bubble at a fraction of the row rather than sizing it to that fraction, so a
 * short message hugs its text. [BoxWithConstraints] turns the fraction into the `widthIn` bound
 * each bubble needs.
 */
@Composable
fun MessageBubble(
    message: ChatMessageEntity,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        if (message.role == ChatRole.USER) {
            UserMessage(
                message = message,
                maxBubbleWidth = maxWidth * USER_BUBBLE_MAX_WIDTH
            )
        } else {
            AssistantMessage(
                message = message,
                maxBubbleWidth = maxWidth * ASSISTANT_BUBBLE_MAX_WIDTH
            )
        }
    }
}

@Composable
private fun UserMessage(
    message: ChatMessageEntity,
    maxBubbleWidth: Dp,
    modifier: Modifier = Modifier
) {
    val coach = LocalCoachColors.current

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = message.content,
            fontSize = 14.5.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .widthIn(max = maxBubbleWidth)
                .clip(UserBubbleShape)
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        )
        // The failed variant — desaturated bubble plus a Retry link — is the error state, and
        // lands with the rest of it. Reporting the status honestly here beats hardcoding
        // "Delivered" onto a message that is still in flight.
        when (message.deliveryStatus) {
            DeliveryStatus.SENT -> Text(
                text = "✓ Delivered",
                fontSize = 11.sp,
                color = coach.success
            )

            DeliveryStatus.SENDING -> Text(
                text = "Sending…",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            DeliveryStatus.FAILED -> Text(
                text = "⚠ Not delivered",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun AssistantMessage(
    message: ChatMessageEntity,
    maxBubbleWidth: Dp,
    modifier: Modifier = Modifier
) {
    val coach = LocalCoachColors.current

    Row(
        modifier = modifier.widthIn(max = maxBubbleWidth),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CoachAvatar(modifier = Modifier.padding(top = 2.dp))
        Column(
            modifier = Modifier
                .clip(AssistantBubbleShape)
                .background(
                    if (message.isPraise) coach.successContainer
                    else MaterialTheme.colorScheme.surface
                )
                .border(
                    width = 1.dp,
                    color = if (message.isPraise) coach.success
                    else MaterialTheme.colorScheme.outline,
                    shape = AssistantBubbleShape
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            if (message.isPraise) {
                Text(
                    text = "PERFECT!",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp,
                    color = coach.onSuccessContainer,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            Text(
                text = message.content,
                fontSize = 14.5.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            message.romanization?.let { romanization ->
                Text(
                    text = romanization,
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 5.dp)
                )
            }
            CopyAction(
                text = message.content,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@Composable
private fun CoachAvatar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondary)
    )
}

@Composable
private fun CopyAction(
    text: String,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val clipboard = LocalClipboard.current

    Row(
        modifier = modifier
            .testTag("CopyResponseButton")
            .clickable {
                coroutineScope.launch {
                    clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("response", text)))
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = "Copy response",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(13.dp)
        )
        Text(
            text = "Copy",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TypingIndicator(modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        CoachAvatar(modifier = Modifier.padding(top = 2.dp))
        Row(
            modifier = Modifier
                .testTag("TypingIndicator")
                .clip(AssistantBubbleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, AssistantBubbleShape)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val transition = rememberInfiniteTransition(label = "typingIndicator")
            repeat(3) { index ->
                val alpha by transition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 600,
                            delayMillis = index * 150,
                            easing = LinearEasing
                        ),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "typingIndicatorDot$index"
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .alpha(alpha)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }
    }
}

/* ------------------------------------------------------------------ composer */

@Composable
fun MessageComposer(
    promptState: TextFieldState,
    onSubmitMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(ComposerFieldShape)
                .background(MaterialTheme.colorScheme.background)
                .border(1.dp, MaterialTheme.colorScheme.outline, ComposerFieldShape)
                .padding(horizontal = 16.dp, vertical = 11.dp)
        ) {
            val hasText = promptState.text.isNotEmpty()

            BasicTextField(
                state = promptState,
                lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 4),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = if (hasText) 26.dp else 0.dp)
                    .testTag("PromptField"),
                decorator = { innerTextField ->
                    if (!hasText) {
                        Text(
                            text = "Nhắn tin bằng tiếng Việt…",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    innerTextField()
                }
            )
            if (hasText) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { promptState.clearText() }
                        .testTag("ClearPromptButton"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear prompt",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
        SendButton(onClick = onSubmitMessage)
    }
}

@Composable
fun SendButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .clickable(onClick = onClick)
            .testTag("SendButton"),
        contentAlignment = Alignment.Center
    ) {
        // The mockup's glyph is a plain solid triangle, which PlayArrow matches exactly —
        // Icons.Default.Send has a swept tail the design does not use.
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Send message",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(22.dp)
        )
    }
}

private val ComposerFieldShape = RoundedCornerShape(22.dp)
private const val ASSISTANT_BUBBLE_MAX_WIDTH = 0.85f
private const val USER_BUBBLE_MAX_WIDTH = 0.78f

/* ------------------------------------------------------------------ previews */

private val sampleMessages = listOf(
    ChatMessageEntity(
        id = 3,
        role = ChatRole.ASSISTANT,
        content = "Câu này đúng ngữ pháp rồi. Giỏi lắm!",
        timestamp = 3L,
        romanization = "Grammatically correct — well done!",
        isPraise = true
    ),
    ChatMessageEntity(
        id = 2,
        role = ChatRole.USER,
        content = "Hôm qua tôi đã đi đến chợ.",
        timestamp = 2L
    ),
    ChatMessageEntity(
        id = 1,
        role = ChatRole.ASSISTANT,
        content = "Hãy thử nói một câu bằng tiếng Việt nhé.",
        timestamp = 1L,
        romanization = "Try saying a sentence in Vietnamese."
    )
)

@Composable
private fun PreviewChat(
    uiState: ChatUiState,
    messages: List<ChatMessageEntity> = sampleMessages,
    darkTheme: Boolean = false
) {
    val flow: Flow<PagingData<ChatMessageEntity>> = remember { flowOf(PagingData.from(messages)) }
    VietAICoachTheme(darkTheme = darkTheme) {
        ChatScreen(
            promptState = rememberTextFieldState(),
            uiState = uiState,
            messages = flow.collectAsLazyPagingItems(),
            onSubmitMessage = { }
        )
    }
}

@Preview(name = "Success · light", showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun ChatScreenSuccessPreview() =
    PreviewChat(ChatUiState(isInitialLoading = false, dialect = "Northern"))

@Preview(
    name = "Success · dark",
    showBackground = true,
    widthDp = 412,
    heightDp = 892,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun ChatScreenSuccessDarkPreview() =
    PreviewChat(ChatUiState(isInitialLoading = false, dialect = "Northern"), darkTheme = true)

@Preview(name = "Typing · light", showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun ChatScreenTypingPreview() = PreviewChat(
    ChatUiState(isInitialLoading = false, isAwaitingReply = true, dialect = "Northern")
)
