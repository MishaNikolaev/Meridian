package com.nmichail.meridian.features.conversation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmichail.meridian.core.network.error.userMessage
import com.nmichail.meridian.core.presentation.launchTry
import com.nmichail.meridian.shared.chat.data.realtime.ChatRealtime
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine
import com.nmichail.meridian.shared.chat.domain.usecase.LoadMessagesUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.MarkReadUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.SendMessageUseCase
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private const val BOT_CHAT = "bot:"

data class ConversationState(
	val title: String,
	val lines: List<ChatLine>,
	val members: Int,
	val bot: Boolean,
	val draft: String,
	val editingId: String?,
	val editingText: String,
	val failure: Int?,
	val sending: Boolean,
)

class ConversationViewModel @Inject constructor(
	private val loadMessagesUseCase: LoadMessagesUseCase,
	private val sendMessageUseCase: SendMessageUseCase,
	private val markReadUseCase: MarkReadUseCase,
	private val realtime: ChatRealtime,
	private val router: ConversationRouter,
) : ViewModel() {

	private val stateFlow = MutableStateFlow(
		ConversationState(
			title = "",
			lines = emptyList(),
			members = 0,
			bot = false,
			draft = "",
			editingId = null,
			editingText = "",
			failure = null,
			sending = false,
		),
	)
	val state: StateFlow<ConversationState> = stateFlow.asStateFlow()
	private var chatId: String? = null
	private var readId: String? = null

	fun attach(id: String, title: String) {
		if (chatId == id) {
			return
		}
		chatId = id
		readId = null
		if (id.startsWith(BOT_CHAT)) {
			stateFlow.value = ConversationState(
				title = title,
				lines = listOf(botGreeting(title)),
				members = 0,
				bot = true,
				draft = "",
				editingId = null,
				editingText = "",
				failure = null,
				sending = false,
			)
			return
		}
		stateFlow.update { it.copy(title = title) }
		realtime.ensureStarted()
		viewModelScope.launchTry {
			realtime.chatIds.collect { changed ->
				if (changed == id) {
					reload(id)
				}
			}
		} catch { _ -> }
		reload(id)
	}

	fun onDraft(value: String) {
		stateFlow.update { it.copy(draft = value) }
	}

	fun beginEdit(line: ChatLine) {
		if (!line.mine) {
			return
		}
		stateFlow.update { it.copy(editingId = line.id, editingText = line.text, draft = line.text) }
	}

	fun cancelEdit() {
		stateFlow.update { it.copy(editingId = null, editingText = "", draft = "") }
	}

	fun send() {
		val id = chatId ?: return
		val text = stateFlow.value.draft.trim()
		if (text.isEmpty() || stateFlow.value.sending) {
			return
		}
		if (stateFlow.value.editingId != null) {
			applyEdit(text)
			return
		}
		if (id.startsWith(BOT_CHAT)) {
			val line = ChatLine(id = "mine-${stateFlow.value.lines.size}", mine = true, text = text, sentAt = Instant.now().toString())
			stateFlow.update { current -> current.copy(draft = "", lines = current.lines + line) }
			return
		}
		stateFlow.update { it.copy(sending = true, draft = "", failure = null) }
		viewModelScope.launchTry {
			sendMessageUseCase(id, text)
			stateFlow.update { it.copy(sending = false) }
			reload(id)
		} catch { error ->
			stateFlow.update { it.copy(sending = false, draft = text, failure = error.userMessage()) }
		}
	}

	fun back() {
		router.navigateBack()
	}

	private fun applyEdit(text: String) {
		val editing = stateFlow.value.editingId ?: return
		stateFlow.update { current ->
			current.copy(
				editingId = null,
				editingText = "",
				draft = "",
				lines = current.lines.map { line -> if (line.id == editing) line.copy(text = text) else line },
			)
		}
	}

	private fun botGreeting(title: String): ChatLine = ChatLine(
		id = "hello",
		mine = false,
		text = "",
		sentAt = Instant.now().toString(),
		author = title,
	)

	private fun reload(id: String) {
		viewModelScope.launchTry {
			val thread = loadMessagesUseCase(id)
			stateFlow.update { it.copy(lines = thread.lines, members = thread.members, failure = null) }
			acknowledge(thread.readUpTo)
		} catch { error ->
			stateFlow.update { it.copy(failure = error.userMessage()) }
		}
	}

	private fun acknowledge(messageId: String?) {
		if (messageId == null || messageId == readId) {
			return
		}
		readId = messageId
		viewModelScope.launchTry {
			markReadUseCase(messageId)
		} catch { _ ->
			if (readId == messageId) {
				readId = null
			}
		}
	}
}
