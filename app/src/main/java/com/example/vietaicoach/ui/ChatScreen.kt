package com.example.vietaicoach.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vietaicoach.ui.model.UiState

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = viewModel()
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    ChatScreen(
        uiState = uiState,
        onSubmitButtonClicked = { viewModel.submitMessage("hello") },
        modifier = modifier
    )
}

@Composable
fun ChatScreen(
    uiState: UiState,
    onSubmitButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onSubmitButtonClicked) {
            Text(text = "Submit")
        }
        Text(
            text = uiState.errorMessage ?: uiState.response
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatScreen(
        uiState = UiState("Hello!"),
        onSubmitButtonClicked = { }
    )
}