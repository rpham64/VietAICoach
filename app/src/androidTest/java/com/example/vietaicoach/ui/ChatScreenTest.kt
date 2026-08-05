package com.example.vietaicoach.ui

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextInput
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.vietaicoach.data.local.model.ChatMessageEntity
import com.example.vietaicoach.data.local.model.ChatRole
import com.example.vietaicoach.ui.model.ChatUiState
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class ChatScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun pagingItemsOf(vararg messages: ChatMessageEntity) =
        flowOf(PagingData.from(messages.toList()))

    @Test
    fun displaysUserAndAssistantMessageBubbles() {
        composeTestRule.setContent {
            ChatScreen(
                promptState = TextFieldState(),
                onClearClicked = {},
                uiState = ChatUiState(isInitialLoading = false),
                messages = pagingItemsOf(
                    ChatMessageEntity(id = 1, role = ChatRole.USER, content = "Hi there", timestamp = 1L),
                    ChatMessageEntity(id = 2, role = ChatRole.ASSISTANT, content = "Hello!", timestamp = 2L)
                ).collectAsLazyPagingItems(),
                onSubmitButtonClicked = {}
            )
        }

        composeTestRule.onNodeWithText("Hi there").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hello!").assertIsDisplayed()
    }

    @Test
    fun submitButtonClickInvokesCallback() {
        var submitted = false
        composeTestRule.setContent {
            ChatScreen(
                promptState = TextFieldState(),
                onClearClicked = {},
                uiState = ChatUiState(isInitialLoading = false),
                messages = pagingItemsOf().collectAsLazyPagingItems(),
                onSubmitButtonClicked = { submitted = true }
            )
        }

        composeTestRule.onNodeWithText("Submit").performClick()

        assert(submitted)
    }

    @Test
    fun submitButtonShowsLoadingIndicatorInsteadOfLabelWhenLoading() {
        composeTestRule.setContent {
            ChatScreen(
                promptState = TextFieldState(),
                onClearClicked = {},
                uiState = ChatUiState(isInitialLoading = false, isAwaitingReply = true),
                messages = pagingItemsOf().collectAsLazyPagingItems(),
                onSubmitButtonClicked = {}
            )
        }

        composeTestRule.onNodeWithText("Submit").assertDoesNotExist()
        composeTestRule.onNodeWithTag("SubmitButtonLoadingIndicator").assertIsDisplayed()
    }

    @Test
    fun displaysTypingIndicatorWhileWaitingForAResponse() {
        composeTestRule.setContent {
            ChatScreen(
                promptState = TextFieldState(),
                onClearClicked = {},
                uiState = ChatUiState(isInitialLoading = false, isAwaitingReply = true),
                messages = pagingItemsOf().collectAsLazyPagingItems(),
                onSubmitButtonClicked = {}
            )
        }

        composeTestRule.onNodeWithTag("TypingIndicator").assertIsDisplayed()
    }

    @Test
    fun doesNotDisplayTypingIndicatorWhenNotLoading() {
        composeTestRule.setContent {
            ChatScreen(
                promptState = TextFieldState(),
                onClearClicked = {},
                uiState = ChatUiState(isInitialLoading = false),
                messages = pagingItemsOf().collectAsLazyPagingItems(),
                onSubmitButtonClicked = {}
            )
        }

        composeTestRule.onNodeWithTag("TypingIndicator").assertDoesNotExist()
    }

    @Test
    fun clearIconClickInvokesCallback() {
        val promptState = TextFieldState(initialText = "Hello")
        composeTestRule.setContent {
            ChatScreen(
                promptState = promptState,
                onClearClicked = { promptState.clearText() },
                uiState = ChatUiState(isInitialLoading = false),
                messages = pagingItemsOf().collectAsLazyPagingItems(),
                onSubmitButtonClicked = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Clear prompt").performClick()

        assert(promptState.text.isEmpty())
    }

    @Test
    fun submitButtonClickScrollsChatBackToTheBottom() {
        val messages = (30 downTo 1).map { i ->
            ChatMessageEntity(id = i.toLong(), role = ChatRole.USER, content = "Message $i", timestamp = i.toLong())
        }.toTypedArray()

        composeTestRule.setContent {
            ChatScreen(
                promptState = TextFieldState(),
                onClearClicked = {},
                uiState = ChatUiState(isInitialLoading = false),
                messages = pagingItemsOf(*messages).collectAsLazyPagingItems(),
                onSubmitButtonClicked = {}
            )
        }

        composeTestRule.onNodeWithTag("MessageList").performScrollToIndex(25)
        composeTestRule.onNodeWithText("Message 30").assertIsNotDisplayed()

        composeTestRule.onNodeWithText("Submit").performClick()

        composeTestRule.onNodeWithText("Message 30").assertIsDisplayed()
    }

    @Test
    fun typingIntoPromptFieldUpdatesPromptState() {
        val promptState = TextFieldState()
        composeTestRule.setContent {
            ChatScreen(
                promptState = promptState,
                onClearClicked = {},
                uiState = ChatUiState(isInitialLoading = false),
                messages = pagingItemsOf().collectAsLazyPagingItems(),
                onSubmitButtonClicked = {}
            )
        }

        composeTestRule.onNode(hasSetTextAction()).performTextInput("Hello")

        assert(promptState.text.toString() == "Hello")
    }
}
