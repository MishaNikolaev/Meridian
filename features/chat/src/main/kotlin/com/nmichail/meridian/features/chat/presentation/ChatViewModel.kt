package com.nmichail.meridian.features.chat.presentation

import com.nmichail.meridian.core.network.error.userMessage
import com.nmichail.meridian.core.presentation.BaseViewModel
import com.nmichail.meridian.core.validation.displayName.DisplayNameValidationItem
import com.nmichail.meridian.core.validation.displayName.ValidateDisplayNameUseCase
import com.nmichail.meridian.core.validation.displayName.isValid
import com.nmichail.meridian.core.validation.username.UsernameValidationItem
import com.nmichail.meridian.core.validation.username.ValidateUsernameUseCase
import com.nmichail.meridian.core.validation.username.isValid
import com.nmichail.meridian.shared.chat.data.realtime.ChatRealtime
import com.nmichail.meridian.shared.chat.domain.entity.ChatSummary
import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.chat.domain.usecase.CreateChatUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.LoadChatsUseCase
import javax.inject.Inject

class ChatViewModel @Inject constructor(
	private val loadChatsUseCase: LoadChatsUseCase,
	private val createChatUseCase: CreateChatUseCase,
	private val validateUsernameUseCase: ValidateUsernameUseCase,
	private val validateDisplayNameUseCase: ValidateDisplayNameUseCase,
	private val realtime: ChatRealtime,
	private val router: ChatRouter,
) : BaseViewModel<ChatState, ChatIntent, Nothing>() {

	override fun initState(): ChatState = ChatState.Initial

	override fun applyIntent(intent: ChatIntent) {
		when (intent) {
			is ChatIntent.LoadInitialData -> handleLoadInitialData()
			is ChatIntent.ChangeCorporate -> handleChangeCorporate(intent.corporate)
			is ChatIntent.ChangeSection -> handleChangeSection(intent.section)
			is ChatIntent.ChangeSearchOpen -> handleChangeSearchOpen()
			is ChatIntent.ChangeSearch -> handleChangeSearch(intent.search)
			is ChatIntent.ChangeCreate -> handleChangeCreate(intent.open)
			is ChatIntent.ChangeTitle -> handleChangeTitle(intent.title)
			is ChatIntent.ChangeUsername -> handleChangeUsername(intent.username)
			is ChatIntent.OpenChat -> handleOpenChat(intent.chat)
			is ChatIntent.OpenBot -> handleOpenBot(intent.id, intent.title)
			is ChatIntent.Submit -> handleSubmit()
			is ChatIntent.NavigateBack -> handleNavigateBack()
		}
	}

	private fun handleLoadInitialData() {
		if (state.value != ChatState.Initial) {
			return
		}
		realtime.ensureStarted()

		launchTry {
			realtime.chatIds.collect { handleRefresh() }
		} catch { _ -> }
		handleRefresh()
	}

	private fun handleChangeCorporate(corporate: Boolean) {
		val current = state.value as? ChatState.Content ?: return
		setState(current.copy(corporate = corporate))
	}

	private fun handleChangeSection(section: ChatSection) {
		val current = state.value as? ChatState.Content ?: return
		setState(current.copy(section = section))
	}

	private fun handleChangeSearchOpen() {
		val current = state.value as? ChatState.Content ?: return
		val open = !current.searchOpen
		val search = if (open) {
			current.search
		} else {
			""
		}

		setState(
			current.copy(
				searchOpen = open,
				search = search,
			),
		)
	}

	private fun handleChangeSearch(search: String) {
		val current = state.value as? ChatState.Content ?: return
		setState(current.copy(search = search))
	}

	private fun handleChangeCreate(open: Boolean) {
		val current = state.value as? ChatState.Content ?: return
		setState(
			current.copy(
				createOpen = open,
				title = DisplayNameValidationItem(),
				username = UsernameValidationItem(),
			),
		)
	}

	private fun handleChangeTitle(title: String) {
		val current = state.value as? ChatState.Content ?: return
		if (current.creating) {
			return
		}
		setState(current.copy(title = validateDisplayNameUseCase(title)))
	}

	private fun handleChangeUsername(username: String) {
		val current = state.value as? ChatState.Content ?: return
		if (current.creating) {
			return
		}
		setState(current.copy(username = validateUsernameUseCase(username)))
	}

	private fun handleOpenChat(chat: ChatSummary) {
		handleOpenConversation(chat.id, chat.title)
	}

	private fun handleOpenBot(id: String, title: String) {
		handleOpenConversation("bot:$id", title)
	}

	private fun handleOpenConversation(chatId: String, title: String) {
		router.openConversation(chatId, title)
	}

	private fun handleNavigateBack() {
		router.navigateBack()
	}

	private fun handleSubmit() {
		val current = state.value as? ChatState.Content ?: return
		if (current.creating) {
			return
		}
		when (current.section) {
			ChatSection.WORK -> handleSubmitGroup(current)
			ChatSection.ALL, ChatSection.PERSONAL, ChatSection.BOTS -> handleSubmitDirect(current)
		}
	}

	private fun handleSubmitGroup(current: ChatState.Content) {
		val title = validateDisplayNameUseCase(current.title.data.trim())
		val username = validateUsernameUseCase(current.username.data.trim())
		if (!title.isValid() || !username.isValid()) {
			setState(current.copy(title = title, username = username))
			return
		}
		handleSendChat(current, ChatType.GROUP, title.data, username.data)
	}

	private fun handleSubmitDirect(current: ChatState.Content) {
		val username = validateUsernameUseCase(current.username.data.trim())
		if (!username.isValid()) {
			setState(current.copy(username = username))
			return
		}
		handleSendChat(current, ChatType.DIRECT, "", username.data)
	}

	private fun handleSendChat(current: ChatState.Content, type: ChatType, title: String, username: String) {
		setState(current.copy(creating = true))
		launchTry {
			val chat = createChatUseCase(type, title, username)
			val content = state.value as? ChatState.Content ?: current
			setState(
				content.copy(
					creating = false,
					createOpen = false,
					title = DisplayNameValidationItem(),
					username = UsernameValidationItem(),
				),
			)
			handleRefresh()
			handleOpenConversation(chat.id, chat.title)
		} catch { error ->
			setState(ChatState.Error(message = error.userMessage()))
		}
	}

	private fun handleRefresh() {
		if (state.value !is ChatState.Content) {
			setState(ChatState.Loading)
		}

		launchTry {
			val chats = loadChatsUseCase()
			when (val current = state.value) {
				is ChatState.Content -> setState(current.copy(chats = chats))
				else -> setState(
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
				)
			}
		} catch { error ->
			setState(ChatState.Error(message = error.userMessage()))
		}
	}
}
