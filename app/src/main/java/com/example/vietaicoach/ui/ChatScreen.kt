package com.example.vietaicoach.ui

import android.content.ClipData
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.vietaicoach.ui.model.ResponseState
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val prompt by viewModel.promptStateFlow.collectAsStateWithLifecycle()
    val responseState by viewModel.responseStateFlow.collectAsStateWithLifecycle()

    ChatScreen(
        prompt = prompt,
        onPromptChanged = { newPrompt -> viewModel.updatePrompt(newPrompt) },
        onClearClicked = { viewModel.updatePrompt("") },
        responseState = responseState,
        onSubmitButtonClicked = { viewModel.submitMessage(prompt) },
        modifier = modifier
    )
}

@Composable
fun ChatScreen(
    prompt: String,
    onPromptChanged: (String) -> Unit,
    onClearClicked: () -> Unit,
    responseState: ResponseState,
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
            ResponseText(responseState = responseState)
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
                value = prompt,
                onValueChange = { newPrompt -> onPromptChanged(newPrompt) },
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
fun ResponseText(
    responseState: ResponseState
) {
    val scrollState = rememberScrollState()

    Text(
        text = responseState.errorMessage ?: responseState.response,
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Gray)
            .padding(16.dp)
            .verticalScroll(scrollState)
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
    ChatScreen(
        prompt = "This is a test prompt",
        onPromptChanged = { },
        onClearClicked = { },
        responseState = ResponseState("Hello!"),
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