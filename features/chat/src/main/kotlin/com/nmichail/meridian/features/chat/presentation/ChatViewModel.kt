package com.nmichail.meridian.features.chat.presentation

import com.nmichail.meridian.core.network.error.ErrorStateConverter
import com.nmichail.meridian.core.network.error.StatusCodes
import com.nmichail.meridian.core.network.error.messageIdOrNull
import com.nmichail.meridian.core.presentation.BaseViewModel
import com.nmichail.meridian.shared.chat.data.realtime.ChatRealtime
import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.entity.ChatDiscussion
import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.chat.domain.usecase.LoadChatsUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.LoadDiscussionsUseCase
import javax.inject.Inject
import kotlinx.coroutines.Job

class ChatViewModel @Inject constructor(
	private val loadChatsUseCase: LoadChatsUseCase,
	private val loadDiscussionsUseCase: LoadDiscussionsUseCase,
	private val realtime: ChatRealtime,
	private val errorStateConverter: ErrorStateConverter,
	private val router: ChatRouter,
) : BaseViewModel<ChatState, ChatIntent, ChatEvent>() {

	private var updatesJob: Job? = null

	override fun initState(): ChatState = ChatState.Initial

	override fun applyIntent(intent: ChatIntent) {
		when (intent) {
			is ChatIntent.LoadInitialData -> handleLoadInitialData()
			is ChatIntent.ChangeCorporate -> handleChangeCorporate(intent.corporate)
			is ChatIntent.ChangeSection   -> handleChangeSection(intent.section)
			is ChatIntent.OpenSearch      -> handleNavigateToSearch()
			is ChatIntent.ChangeCreate    -> handleChangeCreate(intent.open)
			is ChatIntent.OpenChat        -> handleOpenChat(intent.chat)
			is ChatIntent.OpenDiscussion  -> handleNavigateToDiscussion(
				chatId = intent.chatId,
				messageId = intent.messageId,
				title = intent.title,
				corporate = intent.corporate,
				personId = intent.personId,
			)

			is ChatIntent.OpenBot         -> handleOpenBot(intent.id, intent.title)
			is ChatIntent.OpenGroup       -> handleNavigateToGroup()
			is ChatIntent.Retry           -> handleRetry()
			is ChatIntent.ChangeServer    -> handleNavigateToWorkspace()
			is ChatIntent.NavigateBack    -> handleNavigateBack()
		}
	}

	private fun handleLoadInitialData() {
		if (state.value != ChatState.Initial) {
			return
		}
		handleRefresh()
		handleListen()
	}

	private fun handleChangeCorporate(corporate: Boolean) {
		val current = state.value as? ChatState.Content ?: return

		setState(
			current.copy(corporate = corporate)
		)
	}

	private fun handleChangeSection(section: ChatSection) {
		val current = state.value as? ChatState.Content ?: return
		if (current.section == section) {
			return
		}

		setState(current.copy(section = section))
	}

	private fun handleNavigateToSearch() {
		router.navigateToSearch()
	}

	private fun handleChangeCreate(open: Boolean) {
		val current = state.value as? ChatState.Content ?: return

		setState(
			current.copy(createOpen = open)
		)
	}

	private fun handleOpenChat(chat: Chat) {
		handleNavigateToConversation(
			chatId = chat.id,
			title = chat.title,
			corporate = chat.corporate && chat.type == ChatType.DIRECT,
			personId = chat.personId.orEmpty(),
		)
	}

	private fun handleNavigateToDiscussion(
		chatId: String,
		messageId: String,
		title: String,
		corporate: Boolean,
		personId: String,
	) {
		router.navigateToDiscussion(
			chatId = chatId,
			title = title,
			corporate = corporate,
			messageId = messageId,
			personId = personId,
		)
	}

	private fun handleNavigateToGroup() {
		val current = state.value as? ChatState.Content ?: return
		setState(current.copy(createOpen = false))

		router.navigateToGroup()
	}

	private fun handleOpenBot(id: String, title: String) {
		// TODO Сделаю, когда ботов буду делать
		handleNavigateToConversation(
			chatId = "bot:$id",
			title = title,
			corporate = false,
			personId = "",
		)
	}

	private fun handleNavigateToConversation(
		chatId: String,
		title: String,
		corporate: Boolean,
		personId: String,
	) {
		router.navigateToConversation(
			chatId = chatId,
			title = title,
			corporate = corporate,
			personId = personId,
		)
	}

	private fun handleNavigateBack() {
		router.navigateBack()
	}

	private fun handleNavigateToWorkspace() {
		router.navigateToWorkspace()
	}

	private fun handleRetry() {
		handleRefresh()
		handleListen()
	}

	private fun handleListen() {
		if (updatesJob?.isActive == true) {
			return
		}
		realtime.ensureStarted()

		updatesJob = launchTry {
			realtime.collectChats { chatId ->
				handleReload(chatId)
			}
		} catch ::handleError
	}

	private fun handleRefresh() {
		if (state.value is ChatState.Initial) {
			setState(ChatState.Loading)
		}

		launchTry {
			handleReload(chatId = null)
		} catch ::handleError
	}

	private suspend fun handleReload(chatId: String?) {
		val chats = loadChatsUseCase()
		val current = state.value as? ChatState.Content
		val discussions = when {
			chatId == null || current == null -> loadDiscussionsUseCase(chats)

			else                              -> loadDiscussionsUseCase.replaceChat(
				chatId = chatId,
				chats = chats,
				discussions = current.corporateDiscussions + current.externalTabDiscussions,
			)
		}
		handlePublish(chats, discussions)
	}

	private fun handlePublish(chats: List<Chat>, discussions: List<ChatDiscussion>) {
		val corporateChats = chats.filter { chat -> chat.onCorporateTab() }
		val externalTabChats = chats.filter { chat -> !chat.onCorporateTab() }
		val corporateDiscussions = discussions.filter { discussion -> discussion.onCorporateTab() }
		val externalTabDiscussions = discussions.filter { discussion -> !discussion.onCorporateTab() }

		when (val current = state.value) {
			is ChatState.Content -> setState(
				current.copy(
					corporateChats = corporateChats,
					externalTabChats = externalTabChats,
					corporateDiscussions = corporateDiscussions,
					externalTabDiscussions = externalTabDiscussions,
				),
			)

			else                 -> setState(
				ChatState.Content(
					corporate = true,
					section = ChatSection.ALL,
					corporateChats = corporateChats,
					externalTabChats = externalTabChats,
					corporateDiscussions = corporateDiscussions,
					externalTabDiscussions = externalTabDiscussions,
					createOpen = false,
				),
			)
		}
	}

	private fun handleError(error: Exception) {
		val networkError = errorStateConverter.convert(error)
		when (networkError.messageIdOrNull()) {
			StatusCodes.NO_CONNECTION.statusCode -> {
				setEvent(ChatEvent.NoConnection)
			}

			else                                 -> {
				setEvent(ChatEvent.Error)
			}
		}
		setState(ChatState.Error)
	}
}