package com.example.vietaicoach.ui

import android.content.ClipData
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.vietaicoach.data.local.ChatMessageEntity
import com.example.vietaicoach.data.local.ChatRole
import com.example.vietaicoach.ui.model.ResponseState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val responseState by viewModel.responseStateFlow.collectAsStateWithLifecycle()
    val messages = viewModel.messages.collectAsLazyPagingItems()

    ChatScreen(
        promptState = viewModel.promptState,
        onClearClicked = { viewModel.promptState.clearText() },
        responseState = responseState,
        messages = messages,
        onSubmitButtonClicked = { viewModel.submitMessage(viewModel.promptState.text.toString()) },
        modifier = modifier
    )
}

@Composable
fun ChatScreen(
    promptState: TextFieldState,
    onClearClicked: () -> Unit,
    responseState: ResponseState,
    messages: LazyPagingItems<ChatMessageEntity>,
    onSubmitButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            MessageList(messages = messages, modifier = Modifier.fillMaxSize())
            if (responseState.errorMessage != null) {
                ErrorBanner(
                    errorMessage = responseState.errorMessage,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
            CopyButton(
                responseState = responseState,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                state = promptState,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            onClearClicked()
                        }
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
            )
            SubmitButton(
                isLoading = responseState.isLoading,
                onSubmitButtonClicked = onSubmitButtonClicked
            )
        }
    }
}

@Composable
fun MessageList(
    messages: LazyPagingItems<ChatMessageEntity>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.LightGray),
        reverseLayout = true,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            count = messages.itemCount,
            key = messages.itemKey { it.id }
        ) { index ->
            val message = messages[index]
            if (message != null) {
                MessageBubble(message = message, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessageEntity,
    modifier: Modifier = Modifier
) {
    val isUser = message.role == ChatRole.USER

    Row(
        modifier = modifier,
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Text(
            text = message.content,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (isUser) MaterialTheme.colorScheme.primaryContainer else Color.White)
                .padding(12.dp)
        )
    }
}

@Composable
fun ErrorBanner(
    errorMessage: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = errorMessage,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(16.dp)
    )
}

@Composable
fun CopyButton(
    responseState: ResponseState,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val clipboard = LocalClipboard.current

    IconButton(
        onClick = {
            coroutineScope.launch {
                clipboard.setClipEntry(
                    ClipEntry(
                        ClipData.newPlainText(
                            "response",
                            responseState.errorMessage ?: responseState.response
                        )
                    )
                )
            }
        },
        modifier = modifier.padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = null,
        )
    }
}

@Composable
fun SubmitButton(
    isLoading: Boolean,
    onSubmitButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onSubmitButtonClicked,
        modifier = modifier.width(100.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(
                text = "Submit",
                maxLines = 1,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    val previewMessages: Flow<PagingData<ChatMessageEntity>> = flowOf(
        PagingData.from(
            listOf(
                ChatMessageEntity(id = 2, role = ChatRole.ASSISTANT, content = "Hello!", timestamp = 2L),
                ChatMessageEntity(id = 1, role = ChatRole.USER, content = "This is a test prompt", timestamp = 1L)
            )
        )
    )

    ChatScreen(
        promptState = rememberTextFieldState(initialText = "This is a test prompt"),
        onClearClicked = { },
        responseState = ResponseState("Hello!"),
        messages = previewMessages.collectAsLazyPagingItems(),
        onSubmitButtonClicked = { }
    )
}

@Preview(showBackground = true)
@Composable
fun SubmitButtonPreview() {
    SubmitButton(
        isLoading = false,
        onSubmitButtonClicked = { }
    )
}

@Preview(showBackground = true)
@Composable
fun SubmitButtonLoadingPreview() {
    SubmitButton(
        isLoading = true,
        onSubmitButtonClicked = { }
    )
}