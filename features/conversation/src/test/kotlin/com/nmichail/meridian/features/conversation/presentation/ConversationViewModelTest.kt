package com.nmichail.meridian.features.conversation.presentation

import com.nmichail.meridian.core.network.device.DeviceIdentityRepository
import com.nmichail.meridian.core.network.error.presentation.DefaultErrorEventConverter
import com.nmichail.meridian.core.network.error.presentation.DefaultErrorReactionConverter
import com.nmichail.meridian.core.network.error.presentation.ErrorDelegate
import com.nmichail.meridian.core.network.error.presentation.ErrorLogger
import com.nmichail.meridian.core.network.error.presentation.ErrorRouter
import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.testutils.InstantExecutorExtension
import com.nmichail.meridian.testutils.TestCoroutineExtension
import com.nmichail.meridian.core.validation.initials.InitialsUseCase
import com.nmichail.meridian.core.validation.mention.ApplyMentionUseCase
import com.nmichail.meridian.core.validation.mention.EveryoneMentionUseCase
import com.nmichail.meridian.core.validation.mention.MatchMentionUseCase
import com.nmichail.meridian.core.validation.mention.MentionQueryUseCase
import com.nmichail.meridian.shared.chat.data.realtime.ChatRealtime
import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine
import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.chat.domain.entity.ChatPerson
import com.nmichail.meridian.shared.chat.domain.entity.ChatThread
import com.nmichail.meridian.shared.chat.domain.entity.ReactionMemory
import com.nmichail.meridian.shared.chat.domain.usecase.DeleteMessageUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.LoadMessagesUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.MarkReadUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.ReactUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.ReadChatsUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.ReadCachedMessagesUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.ReadReactionsUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.SaveReactionUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.SendMessageUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class, InstantExecutorExtension::class)
class ConversationViewModelTest {
	private val loadMessagesUseCase: LoadMessagesUseCase = mock()
	private val readCachedMessagesUseCase: ReadCachedMessagesUseCase = mock()
	private val readChatsUseCase: ReadChatsUseCase = mock()
	private val sendMessageUseCase: SendMessageUseCase = mock()
	private val markReadUseCase: MarkReadUseCase = mock()
	private val readReactionsUseCase: ReadReactionsUseCase = mock {
		on { invoke() } doReturn ReactionMemory(
			chosen = emptyMap(),
			used = emptyList(),
		)
	}
	private val saveReactionUseCase: SaveReactionUseCase = mock()
	private val reactUseCase: ReactUseCase = mock()
	private val deleteMessageUseCase: DeleteMessageUseCase = mock()
	private val realtime: ChatRealtime = mock {
		on { chatIds } doReturn MutableSharedFlow()
	}
	private val router: ConversationRouter = mock()
	private val viewModel = ConversationViewModel(
		loadMessagesUseCase,
		readCachedMessagesUseCase,
		readChatsUseCase,
		sendMessageUseCase,
		markReadUseCase,
		readReactionsUseCase,
		saveReactionUseCase,
		reactUseCase,
		deleteMessageUseCase,
		MentionQueryUseCase(),
		ApplyMentionUseCase(),
		MatchMentionUseCase(),
		EveryoneMentionUseCase(),
		InitialsUseCase(),
		realtime,
		ErrorDelegate(
			errorLogger = ErrorLogger(),
			defaultErrorReactionConverter = DefaultErrorReactionConverter(),
			defaultErrorEventConverter = DefaultErrorEventConverter(),
			errorRouter = mock<ErrorRouter>(),
			sessionRepository = mock<SessionRepository>(),
			deviceIdentityRepository = mock<DeviceIdentityRepository>(),
		),
		router,
	)
	private val ownLine = ChatLine(
		id = "m1",
		mine = true,
		text = "привет",
		sentAt = null,
		author = null,
		authorId = "",
		replyAuthor = null,
		replyText = null,
		threadRoot = null,
		deleted = false,
		edited = false,
		service = false,
	)
	private val emptyThread = ChatThread(
		lines = emptyList(),
		members = 2,
		readUpTo = null,
		reactions = emptyMap(),
		reactionViewers = emptyMap(),
		people = emptyList(),
		selfName = "",
	)
	private val chief = ChatPerson(
		userId = "chief-id",
		username = "chief",
		displayName = "Волков Сергей",
		role = "",
	)
	private val openAnna = ConversationIntent.LoadInitialData(
		chatId = "1",
		title = "Анна",
		corporate = false,
		rootId = null,
		personId = "",
	)
	private val openBot = ConversationIntent.LoadInitialData(
		chatId = "bot:review",
		title = "Code Review Bot",
		corporate = false,
		rootId = null,
		personId = "",
	)
	private val openPlatform = ConversationIntent.LoadInitialData(
		chatId = "1",
		title = "Платформа",
		corporate = false,
		rootId = null,
		personId = "",
	)

	@Test
	fun `sync titles EXPECT renamed group`() = runTest {
		whenever(loadMessagesUseCase.invoke("1")) doReturn emptyThread
		whenever(readChatsUseCase.invoke()) doReturn listOf(
			Chat(
				id = "1",
				type = ChatType.GROUP,
				title = "Платформа",
				preview = "",
				sentAt = null,
				unread = 0,
				corporate = false,
				mentioned = false,
				personId = null,
				discussionRoot = null,
				discussionText = null,
			),
		)
		viewModel.applyIntent(
			ConversationIntent.LoadInitialData(
				chatId = "1",
				title = "Смена",
				corporate = false,
				rootId = null,
				personId = "",
			),
		)

		viewModel.applyIntent(ConversationIntent.SyncTitles)

		assertEquals("Платформа", (viewModel.state.value as ConversationState.Content).title)
	}

	@Test
	fun `delete EXPECT tombstone sent to everyone`() = runTest {
		whenever(loadMessagesUseCase.invoke("1")) doReturn emptyThread.copy(lines = listOf(ownLine))
		viewModel.applyIntent(openAnna)
		viewModel.applyIntent(ConversationIntent.StartSelection(lineId = "m1"))

		viewModel.applyIntent(ConversationIntent.DeleteSelection)

		verify(deleteMessageUseCase).invoke("m1")
	}

	@Test
	fun `pin EXPECT pinned message`() = runTest {
		viewModel.applyIntent(openBot)

		viewModel.applyIntent(ConversationIntent.Pin(lineId = "hello"))

		assertEquals("hello", (viewModel.state.value as ConversationState.Content).pinnedId)
	}

	@Test
	fun `pin again EXPECT unpinned message`() = runTest {
		viewModel.applyIntent(openBot)
		viewModel.applyIntent(ConversationIntent.Pin(lineId = "hello"))

		viewModel.applyIntent(ConversationIntent.Pin(lineId = "hello"))

		assertEquals(null, (viewModel.state.value as ConversationState.Content).pinnedId)
	}

	@Test
	fun `load bot EXPECT bot chat`() = runTest {
		viewModel.applyIntent(openBot)

		assertEquals(true, (viewModel.state.value as ConversationState.Content).bot)
	}

	@Test
	fun `load bot EXPECT greeting author`() = runTest {
		viewModel.applyIntent(openBot)

		assertEquals(
			"Code Review Bot",
			(viewModel.state.value as ConversationState.Content).lines.first().author,
		)
	}

	@Test
	fun `load same chat EXPECT single load`() = runTest {
		whenever(loadMessagesUseCase.invoke("1")) doReturn emptyThread
		viewModel.applyIntent(openAnna)

		viewModel.applyIntent(openAnna)

		verify(loadMessagesUseCase, times(1)).invoke("1")
	}

	@Test
	fun `change draft EXPECT draft`() = runTest {
		viewModel.applyIntent(openBot)

		viewModel.applyIntent(ConversationIntent.ChangeDraft(draft = "привет"))

		assertEquals("привет", (viewModel.state.value as ConversationState.Content).draft)
	}

	@Test
	fun `edit own line EXPECT edited mark`() = runTest {
		whenever(loadMessagesUseCase.invoke("1")) doReturn emptyThread.copy(lines = listOf(ownLine))
		viewModel.applyIntent(openAnna)
		viewModel.applyIntent(ConversationIntent.BeginEdit(line = ownLine))
		viewModel.applyIntent(ConversationIntent.ChangeDraft(draft = "пока"))

		viewModel.applyIntent(ConversationIntent.Send)

		assertEquals(true, (viewModel.state.value as ConversationState.Content).lines.first().edited)
	}

	@Test
	fun `edit incoming line EXPECT no editing`() = runTest {
		viewModel.applyIntent(openBot)
		val line = (viewModel.state.value as ConversationState.Content).lines.first()

		viewModel.applyIntent(ConversationIntent.BeginEdit(line = line))

		assertEquals(null, (viewModel.state.value as ConversationState.Content).editingId)
	}

	@Test
	fun `send bot message EXPECT sent text`() = runTest {
		viewModel.applyIntent(openBot)
		viewModel.applyIntent(ConversationIntent.ChangeDraft(draft = "привет"))

		viewModel.applyIntent(ConversationIntent.Send)

		assertEquals("привет", (viewModel.state.value as ConversationState.Content).lines.last().text)
	}

	@Test
	fun `send chat message EXPECT reloaded lines`() = runTest {
		val lines = listOf(ownLine.copy(mine = false, text = "ответ"))
		whenever(loadMessagesUseCase.invoke("1")) doReturn emptyThread.copy(lines = lines, readUpTo = "m1")
		viewModel.applyIntent(openAnna)
		viewModel.applyIntent(ConversationIntent.ChangeDraft(draft = "привет"))

		viewModel.applyIntent(ConversationIntent.Send)

		assertEquals(lines, (viewModel.state.value as ConversationState.Content).lines)
	}

	@Test
	fun `reply EXPECT quoted author`() = runTest {
		viewModel.applyIntent(openBot)

		viewModel.applyIntent(
			ConversationIntent.StartReply(
				author = "Анна",
				text = "макет",
			),
		)

		assertEquals("Анна", (viewModel.state.value as ConversationState.Content).replyAuthor)
	}

	@Test
	fun `send reply EXPECT quoted request`() = runTest {
		whenever(loadMessagesUseCase.invoke("1")) doReturn emptyThread
		viewModel.applyIntent(openAnna)
		viewModel.applyIntent(
			ConversationIntent.StartReply(
				author = "Анна",
				text = "макет",
			),
		)
		viewModel.applyIntent(ConversationIntent.ChangeDraft(draft = "да"))

		viewModel.applyIntent(ConversationIntent.Send)

		verify(sendMessageUseCase).invoke(
			"1",
			"да",
			"Анна",
			"макет",
			"",
		)
	}

	@Test
	fun `group header EXPECT group`() = runTest {
		whenever(loadMessagesUseCase.invoke("1")) doReturn emptyThread.copy(
			members = 3,
			people = listOf(chief),
		)
		viewModel.applyIntent(openPlatform)

		viewModel.applyIntent(ConversationIntent.OpenPerson)

		verify(router).navigateToGroup(
			chatId = "1",
			title = "Платформа",
			members = listOf("Волков Сергей"),
			corporate = false,
		)
	}

	@Test
	fun `back EXPECT navigate back`() = runTest {
		viewModel.applyIntent(ConversationIntent.NavigateBack)

		verify(router).navigateBack()
	}

	@Test
	fun `start discussion EXPECT comments`() = runTest {
		whenever(loadMessagesUseCase.invoke("1")) doReturn emptyThread
		viewModel.applyIntent(openAnna)

		viewModel.applyIntent(ConversationIntent.StartDiscussion(lineId = "m1"))

		verify(router).navigateToDiscussion(
			chatId = "1",
			title = "Анна",
			corporate = false,
			messageId = "m1",
			personId = "",
		)
	}

	@Test
	fun `cached messages EXPECT stay when refresh fails`() = runTest {
		val line = ownLine.copy(mine = false)
		whenever(readCachedMessagesUseCase.invoke("1")) doReturn emptyThread.copy(lines = listOf(line))
		whenever(loadMessagesUseCase.invoke("1")) doThrow IllegalStateException("down")

		viewModel.applyIntent(openAnna)

		assertEquals(listOf(line), (viewModel.state.value as ConversationState.Content).lines)
	}

	@Test
	fun `at in draft EXPECT mention query`() = runTest {
		whenever(loadMessagesUseCase.invoke("1")) doReturn emptyThread.copy(people = listOf(chief))
		viewModel.applyIntent(openPlatform)

		viewModel.applyIntent(ConversationIntent.ChangeDraft(draft = "глянь @Ки"))

		assertEquals("Ки", (viewModel.state.value as ConversationState.Content).mentionQuery)
	}

	@Test
	fun `matching name EXPECT mention`() = runTest {
		whenever(loadMessagesUseCase.invoke("1")) doReturn emptyThread.copy(people = listOf(chief))
		viewModel.applyIntent(openPlatform)

		viewModel.applyIntent(ConversationIntent.ChangeDraft(draft = "глянь @вол"))

		assertEquals("chief", (viewModel.state.value as ConversationState.Content).mentions.single().token)
	}

	@Test
	fun `pick mention EXPECT username in draft`() = runTest {
		whenever(loadMessagesUseCase.invoke("1")) doReturn emptyThread.copy(people = listOf(chief))
		viewModel.applyIntent(openPlatform)
		viewModel.applyIntent(ConversationIntent.ChangeDraft(draft = "глянь @Ки"))

		viewModel.applyIntent(ConversationIntent.PickMention(token = "chief"))

		assertEquals("глянь @chief ", (viewModel.state.value as ConversationState.Content).draft)
	}

	@Test
	fun `load EXPECT message marked read`() = runTest {
		whenever(loadMessagesUseCase.invoke("1")) doReturn emptyThread.copy(readUpTo = "m1")

		viewModel.applyIntent(openAnna)

		verify(markReadUseCase).invoke("m1")
	}

	@Test
	fun `same message EXPECT marked once`() = runTest {
		whenever(loadMessagesUseCase.invoke("1")) doReturn emptyThread.copy(readUpTo = "m1")
		viewModel.applyIntent(openAnna)
		viewModel.applyIntent(ConversationIntent.ChangeDraft(draft = "привет"))

		viewModel.applyIntent(ConversationIntent.Send)

		verify(markReadUseCase, times(1)).invoke("m1")
	}

	@Test
	fun `missing read marker EXPECT not marked`() = runTest {
		whenever(loadMessagesUseCase.invoke("1")) doReturn emptyThread

		viewModel.applyIntent(openAnna)

		verify(markReadUseCase, never()).invoke(any())
	}

	@Test
	fun `failed mark EXPECT marked again`() = runTest {
		whenever(loadMessagesUseCase.invoke("1")) doReturn emptyThread.copy(readUpTo = "m1")
		whenever(markReadUseCase.invoke("m1")) doThrow IllegalStateException("down")
		viewModel.applyIntent(openAnna)
		viewModel.applyIntent(ConversationIntent.ChangeDraft(draft = "привет"))

		viewModel.applyIntent(ConversationIntent.Send)

		verify(markReadUseCase, times(2)).invoke("m1")
	}
}
