package com.nmichail.meridian.features.conversation.presentation

import com.nmichail.meridian.core.testutils.TestCoroutineExtension
import com.nmichail.meridian.shared.chat.data.realtime.ChatRealtime
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine
import com.nmichail.meridian.shared.chat.domain.entity.ChatThread
import com.nmichail.meridian.shared.chat.domain.usecase.LoadMessagesUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.MarkReadUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.SendMessageUseCase
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

@ExtendWith(TestCoroutineExtension::class)
class ConversationViewModelTest {

	private val loadMessagesUseCase: LoadMessagesUseCase = mock()
	private val sendMessageUseCase: SendMessageUseCase = mock()
	private val markReadUseCase: MarkReadUseCase = mock()
	private val realtime: ChatRealtime = mock()
	private val router: ConversationRouter = mock()
	private val viewModel = ConversationViewModel(
		loadMessagesUseCase,
		sendMessageUseCase,
		markReadUseCase,
		realtime,
		router,
	)

	@Test
	fun `attach bot EXPECT bot chat`() = runTest {
		viewModel.attach("bot:review", "Code Review Bot")

		assertEquals(true, viewModel.state.value.bot)
	}

	@Test
	fun `attach bot EXPECT greeting author`() = runTest {
		viewModel.attach("bot:review", "Code Review Bot")

		assertEquals("Code Review Bot", viewModel.state.value.lines.first().author)
	}

	@Test
	fun `attach same chat EXPECT single load`() = runTest {
		whenever(realtime.chatIds) doReturn MutableSharedFlow()
		whenever(loadMessagesUseCase.invoke("1")) doReturn ChatThread(lines = emptyList(), members = 2)
		viewModel.attach("1", "Анна")

		viewModel.attach("1", "Анна")

		verify(loadMessagesUseCase, times(1)).invoke("1")
	}

	@Test
	fun `change draft EXPECT draft`() = runTest {
		viewModel.onDraft("привет")

		assertEquals("привет", viewModel.state.value.draft)
	}

	@Test
	fun `edit incoming line EXPECT no editing`() = runTest {
		viewModel.attach("bot:review", "Code Review Bot")

		viewModel.beginEdit(viewModel.state.value.lines.first())

		assertEquals(null, viewModel.state.value.editingId)
	}

	@Test
	fun `send bot message EXPECT sent text`() = runTest {
		viewModel.attach("bot:review", "Code Review Bot")
		viewModel.onDraft("привет")

		viewModel.send()

		assertEquals("привет", viewModel.state.value.lines.last().text)
	}

	@Test
	fun `send chat message EXPECT reloaded lines`() = runTest {
		val lines = listOf(
			ChatLine(id = "m1", mine = false, text = "ответ", sentAt = null),
		)
		whenever(realtime.chatIds) doReturn MutableSharedFlow()
		whenever(loadMessagesUseCase.invoke("1")) doReturn ChatThread(lines = lines, members = 2, readUpTo = "m1")
		viewModel.attach("1", "Анна")
		viewModel.onDraft("привет")

		viewModel.send()

		assertEquals(lines, viewModel.state.value.lines)
	}

	@Test
	fun `back EXPECT navigate back`() = runTest {
		viewModel.back()

		verify(router).navigateBack()
	}
}
