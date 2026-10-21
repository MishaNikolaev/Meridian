package com.nmichail.meridian.features.chat.presentation

import com.nmichail.meridian.core.network.R
import com.nmichail.meridian.core.testutils.InstantExecutorExtension
import com.nmichail.meridian.core.testutils.TestCoroutineExtension
import com.nmichail.meridian.core.testutils.doThrowSafe
import com.nmichail.meridian.core.validation.DefaultValidationState
import com.nmichail.meridian.core.validation.displayName.DisplayNameValidationItem
import com.nmichail.meridian.core.validation.displayName.ValidateDisplayNameUseCase
import com.nmichail.meridian.core.validation.username.UsernameInvalidState
import com.nmichail.meridian.core.validation.username.UsernameValidationItem
import com.nmichail.meridian.core.validation.username.ValidateUsernameUseCase
import com.nmichail.meridian.shared.chat.data.realtime.ChatRealtime
import com.nmichail.meridian.shared.chat.domain.entity.ChatSummary
import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.chat.domain.entity.CreatedChat
import com.nmichail.meridian.shared.chat.domain.usecase.CreateChatUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.LoadChatsUseCase
import java.io.IOException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class, InstantExecutorExtension::class)
class ChatViewModelTest {

	private val loadChatsUseCase: LoadChatsUseCase = mock()
	private val createChatUseCase: CreateChatUseCase = mock()
	private val validateUsernameUseCase: ValidateUsernameUseCase = mock()
	private val validateDisplayNameUseCase: ValidateDisplayNameUseCase = mock()
	private val realtime: ChatRealtime = mock()
	private val router: ChatRouter = mock()
	private val viewModel = ChatViewModel(
		loadChatsUseCase,
		createChatUseCase,
		validateUsernameUseCase,
		validateDisplayNameUseCase,
		realtime,
		router,
	)
	private val chats = listOf(
		ChatSummary(
			id = "1",
			type = ChatType.DIRECT,
			title = "Анна",
			preview = "",
			sentAt = null,
			unread = 0,
		),
	)
	private val validUsername = UsernameValidationItem(
		data = "member",
		validationState = DefaultValidationState.Valid,
	)
	private val invalidUsername = UsernameValidationItem(
		data = "a",
		validationState = DefaultValidationState.Invalid(UsernameInvalidState.INVALID_LENGTH),
	)

	@Test
	fun `load initial data EXPECT content`() = runTest {
		whenever(realtime.chatIds) doReturn MutableSharedFlow()
		whenever(loadChatsUseCase.invoke()) doReturn chats

		viewModel.applyIntent(ChatIntent.LoadInitialData)

		assertEquals(
			ChatState.Content(
				corporate = true,
				section = ChatSection.ALL,
				chats = chats,
				search = "",
				searchOpen = false,
				createOpen = false,
				title = DisplayNameValidationItem(),
				username = UsernameValidationItem(),
				creating = false,
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `load failure EXPECT error`() = runTest {
		whenever(realtime.chatIds) doReturn MutableSharedFlow()
		whenever(loadChatsUseCase.invoke()) doThrowSafe IOException("down")

		viewModel.applyIntent(ChatIntent.LoadInitialData)

		assertEquals(ChatState.Error(message = R.string.error_no_connection), viewModel.state.value)
	}

	@Test
	fun `load again EXPECT single refresh`() = runTest {
		whenever(realtime.chatIds) doReturn MutableSharedFlow()
		whenever(loadChatsUseCase.invoke()) doReturn chats
		viewModel.applyIntent(ChatIntent.LoadInitialData)

		viewModel.applyIntent(ChatIntent.LoadInitialData)

		verify(loadChatsUseCase, times(1)).invoke()
	}

	@Test
	fun `change corporate EXPECT external space`() = runTest {
		whenever(realtime.chatIds) doReturn MutableSharedFlow()
		whenever(loadChatsUseCase.invoke()) doReturn chats
		viewModel.applyIntent(ChatIntent.LoadInitialData)

		viewModel.applyIntent(ChatIntent.ChangeCorporate(corporate = false))

		assertEquals(
			ChatState.Content(
				corporate = false,
				section = ChatSection.ALL,
				chats = chats,
				search = "",
				searchOpen = false,
				createOpen = false,
				title = DisplayNameValidationItem(),
				username = UsernameValidationItem(),
				creating = false,
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `change section EXPECT personal`() = runTest {
		whenever(realtime.chatIds) doReturn MutableSharedFlow()
		whenever(loadChatsUseCase.invoke()) doReturn chats
		viewModel.applyIntent(ChatIntent.LoadInitialData)

		viewModel.applyIntent(ChatIntent.ChangeSection(section = ChatSection.PERSONAL))

		assertEquals(
			ChatState.Content(
				corporate = true,
				section = ChatSection.PERSONAL,
				chats = chats,
				search = "",
				searchOpen = false,
				createOpen = false,
				title = DisplayNameValidationItem(),
				username = UsernameValidationItem(),
				creating = false,
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `change search EXPECT query`() = runTest {
		whenever(realtime.chatIds) doReturn MutableSharedFlow()
		whenever(loadChatsUseCase.invoke()) doReturn chats
		viewModel.applyIntent(ChatIntent.LoadInitialData)

		viewModel.applyIntent(ChatIntent.ChangeSearch(search = "анна"))

		assertEquals(
			ChatState.Content(
				corporate = true,
				section = ChatSection.ALL,
				chats = chats,
				search = "анна",
				searchOpen = false,
				createOpen = false,
				title = DisplayNameValidationItem(),
				username = UsernameValidationItem(),
				creating = false,
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `close search EXPECT empty query`() = runTest {
		whenever(realtime.chatIds) doReturn MutableSharedFlow()
		whenever(loadChatsUseCase.invoke()) doReturn chats
		viewModel.applyIntent(ChatIntent.LoadInitialData)
		viewModel.applyIntent(ChatIntent.ChangeSearchOpen)
		viewModel.applyIntent(ChatIntent.ChangeSearch(search = "анна"))

		viewModel.applyIntent(ChatIntent.ChangeSearchOpen)

		assertEquals(
			ChatState.Content(
				corporate = true,
				section = ChatSection.ALL,
				chats = chats,
				search = "",
				searchOpen = false,
				createOpen = false,
				title = DisplayNameValidationItem(),
				username = UsernameValidationItem(),
				creating = false,
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `open create EXPECT empty fields`() = runTest {
		whenever(realtime.chatIds) doReturn MutableSharedFlow()
		whenever(loadChatsUseCase.invoke()) doReturn chats
		whenever(validateUsernameUseCase.invoke("member")) doReturn validUsername
		viewModel.applyIntent(ChatIntent.LoadInitialData)
		viewModel.applyIntent(ChatIntent.ChangeUsername(username = "member"))

		viewModel.applyIntent(ChatIntent.ChangeCreate(open = true))

		assertEquals(
			ChatState.Content(
				corporate = true,
				section = ChatSection.ALL,
				chats = chats,
				search = "",
				searchOpen = false,
				createOpen = true,
				title = DisplayNameValidationItem(),
				username = UsernameValidationItem(),
				creating = false,
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `change username EXPECT validated username`() = runTest {
		whenever(realtime.chatIds) doReturn MutableSharedFlow()
		whenever(loadChatsUseCase.invoke()) doReturn chats
		whenever(validateUsernameUseCase.invoke("member")) doReturn validUsername
		viewModel.applyIntent(ChatIntent.LoadInitialData)

		viewModel.applyIntent(ChatIntent.ChangeUsername(username = "member"))

		assertEquals(
			ChatState.Content(
				corporate = true,
				section = ChatSection.ALL,
				chats = chats,
				search = "",
				searchOpen = false,
				createOpen = false,
				title = DisplayNameValidationItem(),
				username = validUsername,
				creating = false,
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `open chat EXPECT conversation`() = runTest {
		viewModel.applyIntent(ChatIntent.OpenChat(chat = chats.first()))

		verify(router).openConversation("1", "Анна")
	}

	@Test
	fun `open bot EXPECT bot conversation`() = runTest {
		viewModel.applyIntent(ChatIntent.OpenBot(id = "review", title = "Code Review Bot"))

		verify(router).openConversation("bot:review", "Code Review Bot")
	}

	@Test
	fun `submit invalid login EXPECT invalid username`() = runTest {
		whenever(realtime.chatIds) doReturn MutableSharedFlow()
		whenever(loadChatsUseCase.invoke()) doReturn chats
		whenever(validateUsernameUseCase.invoke("a")) doReturn invalidUsername
		viewModel.applyIntent(ChatIntent.LoadInitialData)
		viewModel.applyIntent(ChatIntent.ChangeUsername(username = "a"))

		viewModel.applyIntent(ChatIntent.Submit)

		assertEquals(
			ChatState.Content(
				corporate = true,
				section = ChatSection.ALL,
				chats = chats,
				search = "",
				searchOpen = false,
				createOpen = false,
				title = DisplayNameValidationItem(),
				username = invalidUsername,
				creating = false,
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `submit direct chat EXPECT created chat`() = runTest {
		whenever(realtime.chatIds) doReturn MutableSharedFlow()
		whenever(loadChatsUseCase.invoke()) doReturn chats
		whenever(validateUsernameUseCase.invoke("member")) doReturn validUsername
		whenever(createChatUseCase.invoke(ChatType.DIRECT, "", "member")) doReturn CreatedChat(id = "2", title = "Анна")
		viewModel.applyIntent(ChatIntent.LoadInitialData)
		viewModel.applyIntent(ChatIntent.ChangeUsername(username = "member"))

		viewModel.applyIntent(ChatIntent.Submit)

		verify(createChatUseCase).invoke(ChatType.DIRECT, "", "member")
	}

	@Test
	fun `submit direct chat EXPECT opened conversation`() = runTest {
		whenever(realtime.chatIds) doReturn MutableSharedFlow()
		whenever(loadChatsUseCase.invoke()) doReturn chats
		whenever(validateUsernameUseCase.invoke("member")) doReturn validUsername
		whenever(createChatUseCase.invoke(ChatType.DIRECT, "", "member")) doReturn CreatedChat(id = "2", title = "Анна")
		viewModel.applyIntent(ChatIntent.LoadInitialData)
		viewModel.applyIntent(ChatIntent.ChangeUsername(username = "member"))

		viewModel.applyIntent(ChatIntent.Submit)

		verify(router).openConversation("2", "Анна")
	}

	@Test
	fun `submit failure EXPECT error`() = runTest {
		whenever(realtime.chatIds) doReturn MutableSharedFlow()
		whenever(loadChatsUseCase.invoke()) doReturn chats
		whenever(validateUsernameUseCase.invoke("member")) doReturn validUsername
		whenever(createChatUseCase.invoke(ChatType.DIRECT, "", "member")) doThrowSafe IOException("down")
		viewModel.applyIntent(ChatIntent.LoadInitialData)
		viewModel.applyIntent(ChatIntent.ChangeUsername(username = "member"))

		viewModel.applyIntent(ChatIntent.Submit)

		assertEquals(ChatState.Error(message = R.string.error_no_connection), viewModel.state.value)
	}

	@Test
	fun `navigate back EXPECT router back`() = runTest {
		viewModel.applyIntent(ChatIntent.NavigateBack)

		verify(router).navigateBack()
	}
}
