package com.nmichail.meridian.features.chat.presentation

import androidx.lifecycle.Observer
import com.nmichail.meridian.core.network.device.DeviceIdentityRepository
import com.nmichail.meridian.core.network.error.GatewayCode
import com.nmichail.meridian.core.network.error.GatewayException
import com.nmichail.meridian.core.network.error.presentation.DefaultErrorEventConverter
import com.nmichail.meridian.core.network.error.presentation.DefaultErrorReactionConverter
import com.nmichail.meridian.core.network.error.presentation.ErrorDelegate
import com.nmichail.meridian.core.network.error.presentation.ErrorLogger
import com.nmichail.meridian.core.network.error.presentation.ErrorRouter
import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.testutils.InstantExecutorExtension
import com.nmichail.meridian.testutils.TestCoroutineExtension
import com.nmichail.meridian.testutils.doThrowSafe
import com.nmichail.meridian.shared.chat.data.realtime.ChatRealtime
import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.chat.domain.usecase.LoadChatsUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.ReadChatsUseCase
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
	private val readChatsUseCase: ReadChatsUseCase = mock()
	private val chatRealtime: ChatRealtime = mock { on { chatIds } doReturn MutableSharedFlow() }
	private val errorDelegate = ErrorDelegate(
		errorLogger = ErrorLogger(),
		defaultErrorReactionConverter = DefaultErrorReactionConverter(),
		defaultErrorEventConverter = DefaultErrorEventConverter(),
		errorRouter = mock<ErrorRouter>(),
		sessionRepository = mock<SessionRepository>(),
		deviceIdentityRepository = mock<DeviceIdentityRepository>(),
	)
	private val router: ChatRouter = mock()
	private val eventObserver: Observer<ChatEvent> = mock()
	private val viewModel = ChatViewModel(
		loadChatsUseCase,
		readChatsUseCase,
		chatRealtime,
		errorDelegate,
		router,
	)
	private val chats = listOf(
		Chat(
			id = "1",
			type = ChatType.DIRECT,
			title = "Анна",
			preview = "",
			sentAt = null,
			unread = 0,
			corporate = true,
			mentioned = false,
			personId = "member",
			discussionRoot = null,
			discussionText = null,
		),
	)
	private val externalChat = Chat(
		id = "ext",
		type = ChatType.DIRECT,
		title = "Кирилл",
		preview = "",
		sentAt = null,
		unread = 0,
		corporate = false,
		mentioned = false,
		personId = "guest",
		discussionRoot = null,
		discussionText = null,
	)
	private val ioException = IOException()
	private val illegalStateException = IllegalStateException()

	@Test
	fun `sync titles EXPECT renamed chat`() = runTest {
		val group = Chat(
			id = "g",
			type = ChatType.GROUP,
			title = "Смена",
			preview = "тема",
			sentAt = "2026-11-01T00:00:00Z",
			unread = 0,
			corporate = true,
			mentioned = false,
			personId = null,
			discussionRoot = "m",
			discussionText = "тема",
		)
		whenever(loadChatsUseCase.invoke()) doReturn listOf(group)
		viewModel.applyIntent(ChatIntent.LoadInitialData)
		whenever(readChatsUseCase.invoke()) doReturn listOf(group.copy(title = "Платформа"))

		viewModel.applyIntent(ChatIntent.SyncTitles)
		val content = viewModel.state.value as ChatState.Content

		assertEquals("Платформа", content.corporateChats.single().title)
	}

	@Test
	fun `sync titles EXPECT renamed discussion`() = runTest {
		val group = Chat(
			id = "g",
			type = ChatType.GROUP,
			title = "Смена",
			preview = "тема",
			sentAt = "2026-11-01T00:00:00Z",
			unread = 0,
			corporate = true,
			mentioned = false,
			personId = null,
			discussionRoot = "m",
			discussionText = "тема",
		)
		whenever(loadChatsUseCase.invoke()) doReturn listOf(group)
		viewModel.applyIntent(ChatIntent.LoadInitialData)
		whenever(readChatsUseCase.invoke()) doReturn listOf(group.copy(title = "Платформа"))

		viewModel.applyIntent(ChatIntent.SyncTitles)
		val content = viewModel.state.value as ChatState.Content
		assertEquals("Платформа", content.corporateDiscussions.single().title)
	}

	@Test
	fun `load initial data EXPECT content`() = runTest {
		whenever(loadChatsUseCase.invoke()) doReturn chats

		viewModel.applyIntent(ChatIntent.LoadInitialData)

		assertEquals(
			ChatState.Content(
				corporate = true,
				section = ChatSection.ALL,
				corporateChats = chats,
				externalTabChats = emptyList(),
				corporateDiscussions = emptyList(),
				externalTabDiscussions = emptyList(),
				createOpen = false,
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `external direct chat EXPECT external list`() = runTest {
		whenever(loadChatsUseCase.invoke()) doReturn chats + externalChat

		viewModel.applyIntent(ChatIntent.LoadInitialData)

		assertEquals(
			ChatState.Content(
				corporate = true,
				section = ChatSection.ALL,
				corporateChats = chats,
				externalTabChats = listOf(externalChat),
				corporateDiscussions = emptyList(),
				externalTabDiscussions = emptyList(),
				createOpen = false,
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `cached chats EXPECT kept when reload fails`() = runTest {
		whenever(readChatsUseCase.invoke()) doReturn chats
		whenever(loadChatsUseCase.invoke()).doThrowSafe(ioException)

		viewModel.applyIntent(ChatIntent.LoadInitialData)

		assertEquals(
			ChatState.Content(
				corporate = true,
				section = ChatSection.ALL,
				corporateChats = chats,
				externalTabChats = emptyList(),
				corporateDiscussions = emptyList(),
				externalTabDiscussions = emptyList(),
				createOpen = false,
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `load failure EXPECT error`() = runTest {
		whenever(loadChatsUseCase.invoke()).doThrowSafe(ioException)

		viewModel.applyIntent(ChatIntent.LoadInitialData)

		assertEquals(ChatState.Error, viewModel.state.value)
	}

	@Test
	fun `session ended EXPECT loading`() = runTest {
		whenever(loadChatsUseCase.invoke()).doThrowSafe(GatewayException(code = GatewayCode.UNAUTHORIZED))

		viewModel.applyIntent(ChatIntent.LoadInitialData)

		assertEquals(ChatState.Loading, viewModel.state.value)
	}

	@Test
	fun `refresh failure EXPECT no connection event`() = runTest {
		whenever(loadChatsUseCase.invoke()).doReturn(chats) doThrowSafe (ioException)
		viewModel.event.observeForever(eventObserver)
		viewModel.applyIntent(ChatIntent.LoadInitialData)

		viewModel.applyIntent(ChatIntent.Retry)

		verify(eventObserver).onChanged(ChatEvent.NoConnection)
	}

	@Test
	fun `refresh failure EXPECT unknown event`() = runTest {
		whenever(loadChatsUseCase.invoke()).doReturn(chats) doThrowSafe (illegalStateException)
		viewModel.event.observeForever(eventObserver)
		viewModel.applyIntent(ChatIntent.LoadInitialData)

		viewModel.applyIntent(ChatIntent.Retry)

		verify(eventObserver).onChanged(ChatEvent.Error)
	}

	@Test
	fun `refresh failure EXPECT content`() = runTest {
		whenever(loadChatsUseCase.invoke()).doReturn(chats) doThrowSafe (ioException)
		viewModel.applyIntent(ChatIntent.LoadInitialData)

		viewModel.applyIntent(ChatIntent.Retry)

		assertEquals(
			ChatState.Content(
				corporate = true,
				section = ChatSection.ALL,
				corporateChats = chats,
				externalTabChats = emptyList(),
				corporateDiscussions = emptyList(),
				externalTabDiscussions = emptyList(),
				createOpen = false,
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `retry EXPECT chats`() = runTest {
		whenever(loadChatsUseCase.invoke()).doThrowSafe(ioException).doReturn(chats)
		viewModel.applyIntent(ChatIntent.LoadInitialData)

		viewModel.applyIntent(ChatIntent.Retry)

		assertEquals(
			ChatState.Content(
				corporate = true,
				section = ChatSection.ALL,
				corporateChats = chats,
				externalTabChats = emptyList(),
				corporateDiscussions = emptyList(),
				externalTabDiscussions = emptyList(),
				createOpen = false,
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `change server EXPECT workspace`() = runTest {
		viewModel.applyIntent(ChatIntent.ChangeServer)

		verify(router).navigateToWorkspace()
	}

	@Test
	fun `load again EXPECT single refresh`() = runTest {
		whenever(loadChatsUseCase.invoke()) doReturn chats
		viewModel.applyIntent(ChatIntent.LoadInitialData)

		viewModel.applyIntent(ChatIntent.LoadInitialData)

		verify(loadChatsUseCase, times(1)).invoke()
	}

	@Test
	fun `change corporate EXPECT external space`() = runTest {
		whenever(loadChatsUseCase.invoke()) doReturn chats
		viewModel.applyIntent(ChatIntent.LoadInitialData)

		viewModel.applyIntent(ChatIntent.ChangeCorporate(corporate = false))

		assertEquals(
			ChatState.Content(
				corporate = false,
				section = ChatSection.ALL,
				corporateChats = chats,
				externalTabChats = emptyList(),
				corporateDiscussions = emptyList(),
				externalTabDiscussions = emptyList(),
				createOpen = false,
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `open search EXPECT router`() = runTest {
		viewModel.applyIntent(ChatIntent.OpenSearch)

		verify(router).navigateToSearch()
	}

	@Test
	fun `open create EXPECT menu`() = runTest {
		whenever(loadChatsUseCase.invoke()) doReturn chats
		viewModel.applyIntent(ChatIntent.LoadInitialData)

		viewModel.applyIntent(ChatIntent.ChangeCreate(open = true))

		assertEquals(
			ChatState.Content(
				corporate = true,
				section = ChatSection.ALL,
				corporateChats = chats,
				externalTabChats = emptyList(),
				corporateDiscussions = emptyList(),
				externalTabDiscussions = emptyList(),
				createOpen = true,
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `open chat EXPECT conversation`() = runTest {
		viewModel.applyIntent(ChatIntent.OpenChat(chat = chats.first()))

		verify(router).navigateToConversation(
			chatId = "1",
			title = "Анна",
			corporate = true,
			personId = "member",
		)
	}

	@Test
	fun `open bot EXPECT bot conversation`() = runTest {
		viewModel.applyIntent(ChatIntent.OpenBot(id = "review", title = "Code Review Bot"))

		verify(router).navigateToConversation(
			chatId = "bot:review",
			title = "Code Review Bot",
			corporate = false,
			personId = "",
		)
	}

	@Test
	fun `navigate back EXPECT router back`() = runTest {
		viewModel.applyIntent(ChatIntent.NavigateBack)

		verify(router).navigateBack()
	}

	@Test
	fun `load EXPECT discussion is the thread`() = runTest {
		val threadChat = chats.first().copy(
			preview = "комментарий",
			sentAt = "2026-10-22T12:00:00Z",
			discussionRoot = "root",
			discussionText = "тема обсуждения",
		)
		whenever(loadChatsUseCase.invoke()) doReturn listOf(threadChat)

		viewModel.applyIntent(ChatIntent.LoadInitialData)

		assertEquals(
			ChatState.Content(
				corporate = true,
				section = ChatSection.ALL,
				corporateChats = listOf(threadChat),
				externalTabChats = emptyList(),
				corporateDiscussions = listOf(threadChat).discussions(),
				externalTabDiscussions = emptyList(),
				createOpen = false,
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `open discussion EXPECT person`() = runTest {
		viewModel.applyIntent(
			ChatIntent.OpenDiscussion(
				chatId = "1",
				messageId = "root",
				title = "Анна",
				corporate = true,
				personId = "member",
			),
		)

		verify(router).navigateToDiscussion(
			chatId = "1",
			title = "Анна",
			corporate = true,
			messageId = "root",
			personId = "member",
		)
	}
}