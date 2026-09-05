package com.example.vietaicoach.ui

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextInput
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.vietaicoach.data.local.model.ChatMessageEntity
import com.example.vietaicoach.data.local.model.ChatRole
import com.example.vietaicoach.data.local.model.DeliveryStatus
import com.example.vietaicoach.ui.model.ChatUiState
import com.example.vietaicoach.ui.theme.VietAICoachTheme
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

private val SettledLoadStates = LoadStates(
    refresh = LoadState.NotLoading(endOfPaginationReached = true),
    prepend = LoadState.NotLoading(endOfPaginationReached = true),
    append = LoadState.NotLoading(endOfPaginationReached = true)
)

class ChatScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setChatScreen(
        promptState: TextFieldState = TextFieldState(),
        uiState: ChatUiState = ChatUiState(isInitialLoading = false),
        vararg messages: ChatMessageEntity,
        onSubmitMessage: () -> Unit = {},
        onRetryMessage: (Long) -> Unit = {},
        onMessagesLoaded: () -> Unit = {}
    ) {
        // Explicit load states matter: PagingData.from(list) alone leaves refresh stuck in
        // Loading, so the screen would never see paging settle.
        val flow = flowOf(PagingData.from(messages.toList(), SettledLoadStates))
        composeTestRule.setContent {
            VietAICoachTheme {
                ChatScreen(
                    promptState = promptState,
                    uiState = uiState,
                    messages = flow.collectAsLazyPagingItems(),
                    onSubmitMessage = onSubmitMessage,
                    onRetryMessage = onRetryMessage,
                    onMessagesLoaded = onMessagesLoaded
                )
            }
        }
    }

    @Test
    fun displaysUserAndAssistantMessageBubbles() {
        setChatScreen(
            messages = arrayOf(
                ChatMessageEntity(id = 1, role = ChatRole.USER, content = "Hi there", timestamp = 1L),
                ChatMessageEntity(id = 2, role = ChatRole.ASSISTANT, content = "Hello!", timestamp = 2L)
            )
        )

        composeTestRule.onNodeWithText("Hi there").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hello!").assertIsDisplayed()
    }

    @Test
    fun displaysTheBrandedHeaderWithTheActiveDialect() {
        setChatScreen(uiState = ChatUiState(isInitialLoading = false, dialect = "Northern"))

        composeTestRule.onNodeWithText("Chào Bạn").assertIsDisplayed()
        composeTestRule.onNodeWithText("Practicing · Northern dialect").assertIsDisplayed()
    }

    @Test
    fun displaysTheRomanizationGlossBeneathAnAssistantMessage() {
        setChatScreen(
            messages = arrayOf(
                ChatMessageEntity(
                    id = 1,
                    role = ChatRole.ASSISTANT,
                    content = "Hãy thử nói một câu bằng tiếng Việt nhé.",
                    timestamp = 1L,
                    romanization = "Try saying a sentence in Vietnamese."
                )
            )
        )

        composeTestRule.onNodeWithText("Try saying a sentence in Vietnamese.").assertIsDisplayed()
    }

    @Test
    fun displaysThePraiseLabelOnlyWhenTheCoachConfirmedTheSentence() {
        setChatScreen(
            messages = arrayOf(
                ChatMessageEntity(
                    id = 1,
                    role = ChatRole.ASSISTANT,
                    content = "Giỏi lắm!",
                    timestamp = 1L,
                    isPraise = true
                )
            )
        )

        composeTestRule.onNodeWithText("PERFECT!").assertIsDisplayed()
    }

    @Test
    fun doesNotDisplayThePraiseLabelOnAnOrdinaryReply() {
        setChatScreen(
            messages = arrayOf(
                ChatMessageEntity(id = 1, role = ChatRole.ASSISTANT, content = "Xin chào", timestamp = 1L)
            )
        )

        composeTestRule.onNodeWithText("PERFECT!").assertDoesNotExist()
    }

    @Test
    fun doesNotDisplayACopyActionOnUserMessages() {
        setChatScreen(
            messages = arrayOf(
                ChatMessageEntity(id = 1, role = ChatRole.USER, content = "Xin chào", timestamp = 1L)
            )
        )

        composeTestRule.onNodeWithTag("CopyResponseButton").assertDoesNotExist()
    }

    @Test
    fun displaysACopyActionOnAssistantMessages() {
        setChatScreen(
            messages = arrayOf(
                ChatMessageEntity(id = 1, role = ChatRole.ASSISTANT, content = "Xin chào", timestamp = 1L)
            )
        )

        composeTestRule.onNodeWithTag("CopyResponseButton").assertIsDisplayed()
    }

    @Test
    fun inFlightMessagesAreNotReportedAsDelivered() {
        setChatScreen(
            messages = arrayOf(
                ChatMessageEntity(
                    id = 1,
                    role = ChatRole.USER,
                    content = "Xin chào",
                    timestamp = 1L,
                    deliveryStatus = DeliveryStatus.SENDING
                )
            )
        )

        composeTestRule.onNodeWithText("Sending…").assertIsDisplayed()
        composeTestRule.onNodeWithText("✓ Delivered").assertDoesNotExist()
    }

    @Test
    fun deliveredMessagesAreMarkedAsSuch() {
        setChatScreen(
            messages = arrayOf(
                ChatMessageEntity(
                    id = 1,
                    role = ChatRole.USER,
                    content = "Xin chào",
                    timestamp = 1L,
                    deliveryStatus = DeliveryStatus.SENT
                )
            )
        )

        composeTestRule.onNodeWithText("✓ Delivered").assertIsDisplayed()
    }

    @Test
    fun failedMessagesAreMarkedAsNotDelivered() {
        setChatScreen(
            messages = arrayOf(
                ChatMessageEntity(
                    id = 1,
                    role = ChatRole.USER,
                    content = "Xin chào",
                    timestamp = 1L,
                    deliveryStatus = DeliveryStatus.FAILED
                )
            )
        )

        composeTestRule.onNodeWithText("⚠ Not delivered").assertIsDisplayed()
    }

    @Test
    fun sendButtonClickInvokesCallback() {
        var submitted = false
        setChatScreen(onSubmitMessage = { submitted = true })

        composeTestRule.onNodeWithTag("SendButton").performClick()

        assert(submitted)
    }

    @Test
    fun displaysTypingIndicatorWhileWaitingForAResponse() {
        setChatScreen(uiState = ChatUiState(isInitialLoading = false, isAwaitingReply = true))

        composeTestRule.onNodeWithTag("TypingIndicator").assertIsDisplayed()
    }

    @Test
    fun doesNotDisplayTypingIndicatorWhenNotLoading() {
        setChatScreen()

        composeTestRule.onNodeWithTag("TypingIndicator").assertDoesNotExist()
    }

    @Test
    fun clearAffordanceAppearsOnlyOnceThePromptHasText() {
        setChatScreen(promptState = TextFieldState())

        composeTestRule.onNodeWithTag("ClearPromptButton").assertDoesNotExist()
    }

    @Test
    fun clearAffordanceClickEmptiesThePrompt() {
        val promptState = TextFieldState(initialText = "Hello")
        setChatScreen(promptState = promptState)

        composeTestRule.onNodeWithTag("ClearPromptButton").performClick()

        composeTestRule.runOnIdle { assert(promptState.text.isEmpty()) }
    }

    @Test
    fun typingIntoPromptFieldUpdatesPromptState() {
        val promptState = TextFieldState()
        setChatScreen(promptState = promptState)

        composeTestRule.onNodeWithTag("PromptField").performTextInput("Hello")

        composeTestRule.runOnIdle { assert(promptState.text.toString() == "Hello") }
    }

    @Test
    fun sendButtonClickScrollsChatBackToTheBottom() {
        val messages = (30 downTo 1).map { i ->
            ChatMessageEntity(id = i.toLong(), role = ChatRole.USER, content = "Message $i", timestamp = i.toLong())
        }.toTypedArray()

        setChatScreen(messages = messages)

        composeTestRule.onNodeWithTag("MessageList").performScrollToIndex(25)
        composeTestRule.onNodeWithText("Message 30").assertIsNotDisplayed()

        composeTestRule.onNodeWithTag("SendButton").performClick()

        composeTestRule.onNodeWithText("Message 30").assertIsDisplayed()
    }

    @Test
    fun displaysTheSkeletonInsteadOfTheMessageListWhileLoading() {
        setChatScreen(uiState = ChatUiState(isInitialLoading = true))

        composeTestRule.onNodeWithTag("MessageListSkeleton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("MessageList").assertDoesNotExist()
    }

    @Test
    fun displaysTheComposerPlaceholderInsteadOfTheRealComposerWhileLoading() {
        setChatScreen(uiState = ChatUiState(isInitialLoading = true))

        composeTestRule.onNodeWithTag("ComposerSkeleton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("PromptField").assertDoesNotExist()
        composeTestRule.onNodeWithTag("SendButton").assertDoesNotExist()
    }

    @Test
    fun headerReportsLoadingInsteadOfTheDialectWhileLoading() {
        setChatScreen(uiState = ChatUiState(isInitialLoading = true, dialect = "Northern"))

        composeTestRule.onNodeWithText("Loading conversation\u2026").assertIsDisplayed()
        composeTestRule.onNodeWithText("Practicing \u00b7 Northern dialect").assertDoesNotExist()
    }

    @Test
    fun swapsTheSkeletonForTheMessageListOnceLoadingFinishes() {
        setChatScreen(
            uiState = ChatUiState(isInitialLoading = false),
            messages = arrayOf(
                ChatMessageEntity(id = 1, role = ChatRole.USER, content = "Xin ch\u00e0o", timestamp = 1L)
            )
        )

        composeTestRule.onNodeWithTag("MessageListSkeleton").assertDoesNotExist()
        composeTestRule.onNodeWithTag("MessageList").assertIsDisplayed()
    }

    @Test
    fun reportsMessagesLoadedOncePagingSettles() {
        var loaded = false
        setChatScreen(onMessagesLoaded = { loaded = true })

        // Paging has to emit and the effect has to run, neither of which is done at first idle.
        composeTestRule.waitUntil(timeoutMillis = 5_000) { loaded }
    }

    @Test
    fun failedMessagesOfferARetryAffordance() {
        setChatScreen(
            messages = arrayOf(
                ChatMessageEntity(
                    id = 1,
                    role = ChatRole.USER,
                    content = "Xin ch\u00e0o",
                    timestamp = 1L,
                    deliveryStatus = DeliveryStatus.FAILED
                )
            )
        )

        composeTestRule.onNodeWithTag("RetryMessageButton").assertIsDisplayed()
    }

    @Test
    fun retryIsOfferedOnlyForMessagesThatActuallyFailed() {
        setChatScreen(
            messages = arrayOf(
                ChatMessageEntity(
                    id = 1,
                    role = ChatRole.USER,
                    content = "Xin ch\u00e0o",
                    timestamp = 1L,
                    deliveryStatus = DeliveryStatus.SENT
                ),
                ChatMessageEntity(
                    id = 2,
                    role = ChatRole.USER,
                    content = "T\u00f4i mu\u1ed1n \u0111\u1eb7t b\u00e0n.",
                    timestamp = 2L,
                    deliveryStatus = DeliveryStatus.SENDING
                )
            )
        )

        composeTestRule.onNodeWithTag("RetryMessageButton").assertDoesNotExist()
    }

    @Test
    fun retryClickReportsTheIdOfTheFailedMessage() {
        var retried: Long? = null
        setChatScreen(
            messages = arrayOf(
                ChatMessageEntity(
                    id = 42,
                    role = ChatRole.USER,
                    content = "Xin ch\u00e0o",
                    timestamp = 1L,
                    deliveryStatus = DeliveryStatus.FAILED
                )
            ),
            onRetryMessage = { retried = it }
        )

        composeTestRule.onNodeWithTag("RetryMessageButton").performClick()

        composeTestRule.runOnIdle { assert(retried == 42L) }
    }
}
