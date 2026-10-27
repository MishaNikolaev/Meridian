package com.nmichail.meridian.features.chat.presentation

import androidx.lifecycle.Observer
import com.nmichail.meridian.core.network.error.ErrorStateConverter
import com.nmichail.meridian.core.testutils.InstantExecutorExtension
import com.nmichail.meridian.core.testutils.TestCoroutineExtension
import com.nmichail.meridian.core.testutils.doThrowSafe
import com.nmichail.meridian.shared.chat.data.realtime.ChatRealtime
import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.entity.ChatDiscussion
import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.chat.domain.usecase.LoadChatsUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.LoadDiscussionsUseCase
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class, InstantExecutorExtension::class)
class ChatViewModelTest {

	private val loadChatsUseCase: LoadChatsUseCase = mock()
	private val loadDiscussionsUseCase: LoadDiscussionsUseCase = mock()
	private val realtime: ChatRealtime = mock()
	private val errorStateConverter = ErrorStateConverter()
	private val router: ChatRouter = mock()
	private val eventObserver: Observer<ChatEvent> = mock()
	private val viewModel = ChatViewModel(
		loadChatsUseCase,
		loadDiscussionsUseCase,
		realtime,
		errorStateConverter,
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
			personId = "member",
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
		personId = "guest",
	)

	private val discussion = ChatDiscussion(
		chatId = "1",
		messageId = "root",
		title = "Анна",
		text = "тема обсуждения",
		comment = "Анна: комментарий",
		sentAt = "2026-10-22T12:00:00Z",
		corporate = true,
		type = ChatType.DIRECT,
		personId = null,
	)

	private val ioException = IOException()
	private val illegalStateException = IllegalStateException()

	@Test
	fun `load initial data EXPECT content`() = runTest {
		whenever(loadChatsUseCase.invoke()) doReturn chats
		whenever(loadDiscussionsUseCase.invoke(any())) doReturn emptyList()

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
		whenever(loadDiscussionsUseCase.invoke(any())) doReturn emptyList()

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
	fun `load failure EXPECT error`() = runTest {
		whenever(loadChatsUseCase.invoke()).doThrowSafe(ioException)

		viewModel.applyIntent(ChatIntent.LoadInitialData)

		assertEquals(ChatState.Error, viewModel.state.value)
	}

	@Test
	fun `load failure EXPECT no connection event`() = runTest {
		whenever(loadChatsUseCase.invoke()).doThrowSafe(ioException)
		viewModel.event.observeForever(eventObserver)

		viewModel.applyIntent(ChatIntent.LoadInitialData)

		verify(eventObserver).onChanged(ChatEvent.NoConnection)
	}

	@Test
	fun `load failure EXPECT unknown event`() = runTest {
		whenever(loadChatsUseCase.invoke()).doThrowSafe(illegalStateException)
		viewModel.event.observeForever(eventObserver)

		viewModel.applyIntent(ChatIntent.LoadInitialData)

		verify(eventObserver).onChanged(ChatEvent.Error)
	}

	@Test
	fun `messages failure EXPECT error`() = runTest {
		whenever(loadChatsUseCase.invoke()) doReturn chats
		whenever(loadDiscussionsUseCase.invoke(any())).doThrowSafe(ioException)

		viewModel.applyIntent(ChatIntent.LoadInitialData)

		assertEquals(ChatState.Error, viewModel.state.value)
	}

	@Test
	fun `retry EXPECT chats`() = runTest {
		whenever(loadChatsUseCase.invoke()).doThrowSafe(ioException).doReturn(chats)
		whenever(loadDiscussionsUseCase.invoke(any())) doReturn emptyList()
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
		whenever(loadDiscussionsUseCase.invoke(any())) doReturn emptyList()
		viewModel.applyIntent(ChatIntent.LoadInitialData)

		viewModel.applyIntent(ChatIntent.LoadInitialData)

		verify(loadChatsUseCase, times(1)).invoke()
	}

	@Test
	fun `change corporate EXPECT external space`() = runTest {
		whenever(loadChatsUseCase.invoke()) doReturn chats
		whenever(loadDiscussionsUseCase.invoke(any())) doReturn emptyList()
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
	fun `change section EXPECT catalog`() = runTest {
		whenever(loadChatsUseCase.invoke()) doReturn chats
		whenever(loadDiscussionsUseCase.invoke(any())) doReturn emptyList()
		viewModel.applyIntent(ChatIntent.LoadInitialData)

		viewModel.applyIntent(ChatIntent.ChangeSection(section = ChatSection.CATALOG))

		assertEquals(
			ChatState.Content(
				corporate = true,
				section = ChatSection.CATALOG,
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
		whenever(loadDiscussionsUseCase.invoke(any())) doReturn emptyList()
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
		whenever(loadChatsUseCase.invoke()) doReturn chats
		whenever(loadDiscussionsUseCase.invoke(any())) doReturn listOf(discussion)

		viewModel.applyIntent(ChatIntent.LoadInitialData)

		assertEquals(
			ChatState.Content(
				corporate = true,
				section = ChatSection.ALL,
				corporateChats = chats,
				externalTabChats = emptyList(),
				corporateDiscussions = listOf(discussion),
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