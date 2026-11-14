package com.nmichail.meridian.features.conversation.presentation

import com.nmichail.meridian.core.network.error.presentation.BlockingErrorProcessResult
import com.nmichail.meridian.core.network.error.presentation.DefaultErrorEvent
import com.nmichail.meridian.core.network.error.presentation.ErrorDelegate
import com.nmichail.meridian.core.network.error.presentation.NonBlockingErrorProcessResult
import com.nmichail.meridian.core.network.error.userMessage
import com.nmichail.meridian.core.presentation.BaseViewModel
import com.nmichail.meridian.core.validation.initials.InitialsUseCase
import com.nmichail.meridian.core.validation.mention.ApplyMentionUseCase
import com.nmichail.meridian.core.validation.mention.EveryoneMentionUseCase
import com.nmichail.meridian.core.validation.mention.MatchMentionUseCase
import com.nmichail.meridian.core.validation.mention.MentionQueryUseCase
import com.nmichail.meridian.features.conversation.domain.entity.toMention
import com.nmichail.meridian.features.conversation.presentation.compose.component.days
import com.nmichail.meridian.features.conversation.presentation.compose.component.keepingEdits
import com.nmichail.meridian.features.conversation.presentation.compose.component.markDeleted
import com.nmichail.meridian.features.conversation.presentation.compose.component.mineGlyph
import com.nmichail.meridian.features.conversation.presentation.compose.component.reactionGlyph
import com.nmichail.meridian.features.conversation.presentation.compose.component.reactionGlyphs
import com.nmichail.meridian.features.conversation.presentation.compose.component.usedGlyph
import com.nmichail.meridian.shared.chat.data.realtime.ChatRealtime
import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine
import com.nmichail.meridian.shared.chat.domain.entity.ChatPerson
import com.nmichail.meridian.shared.chat.domain.entity.ChatReaction
import com.nmichail.meridian.shared.chat.domain.usecase.DeleteMessageUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.LoadMessagesUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.ReadChatsUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.ReadCachedMessagesUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.MarkReadUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.ReactUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.ReadReactionsUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.SaveReactionUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.SendMessageUseCase
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

class ConversationViewModel @Inject constructor(
	private val loadMessagesUseCase: LoadMessagesUseCase,
	private val readCachedMessagesUseCase: ReadCachedMessagesUseCase,
	private val readChatsUseCase: ReadChatsUseCase,
	private val sendMessageUseCase: SendMessageUseCase,
	private val markReadUseCase: MarkReadUseCase,
	private val readReactionsUseCase: ReadReactionsUseCase,
	private val saveReactionUseCase: SaveReactionUseCase,
	private val reactUseCase: ReactUseCase,
	private val deleteMessageUseCase: DeleteMessageUseCase,
	private val mentionQueryUseCase: MentionQueryUseCase,
	private val applyMentionUseCase: ApplyMentionUseCase,
	private val matchMentionUseCase: MatchMentionUseCase,
	private val everyoneMentionUseCase: EveryoneMentionUseCase,
	private val initialsUseCase: InitialsUseCase,
	private val realtime: ChatRealtime,
	private val errorDelegate: ErrorDelegate,
	private val router: ConversationRouter,
) : BaseViewModel<ConversationState, ConversationIntent, ConversationEvent>() {
	private var markedChatId: String? = null

	private var markedMessageId: String? = null

	override fun initState(): ConversationState = ConversationState.Initial

	override fun applyIntent(intent: ConversationIntent) {
		when (intent) {
			is ConversationIntent.LoadInitialData    -> handleLoadInitialData(intent)
			is ConversationIntent.ChangeDraft        -> handleChangeDraft(intent.draft)
			is ConversationIntent.PickMention        -> handlePickMention(intent.token)
			is ConversationIntent.BeginEdit          -> handleBeginEdit(intent.line)
			is ConversationIntent.CancelEdit         -> handleCancelEdit()
			is ConversationIntent.StartReply         -> handleStartReply(intent.author, intent.text)
			is ConversationIntent.CancelReply        -> handleCancelReply()
			is ConversationIntent.Send               -> handleSend()
			is ConversationIntent.NavigateBack       -> router.navigateBack()
			is ConversationIntent.SyncTitles         -> handleSyncTitles()
			is ConversationIntent.OpenPerson         -> handleOpenPerson()
			is ConversationIntent.OpenMessage        -> handleOpenMessage(intent.lineId)
			is ConversationIntent.DismissMessageMenu -> handleDismissMessageMenu()
			is ConversationIntent.PickReaction       -> handlePickReaction(intent.lineId, intent.reaction)
			is ConversationIntent.Pin                -> handlePin(intent.lineId)
			is ConversationIntent.StartDiscussion    -> handleStartDiscussion(intent.lineId)
			is ConversationIntent.StartSelection     -> handleStartSelection(intent.lineId)
			is ConversationIntent.ClearSelection     -> handleClearSelection()
			is ConversationIntent.DeleteSelection    -> handleDeleteSelection()
		}
	}

	private fun handleLoadInitialData(load: ConversationIntent.LoadInitialData) {
		if (state.value != ConversationState.Initial) {
			return
		}

		if (load.chatId.startsWith("bot:")) {
			handleOpenBot(
				chatId = load.chatId.removePrefix("bot:"),
				title = load.title,
				corporate = load.corporate,
				rootId = load.rootId,
				personId = load.personId,
			)

			return
		}

		handleOpenChat(
			chatId = load.chatId,
			title = load.title,
			corporate = load.corporate,
			rootId = load.rootId,
			personId = load.personId,
		)
	}

	private fun handleOpenBot(
		chatId: String,
		title: String,
		corporate: Boolean,
		rootId: String?,
		personId: String,
	) {
		handlePublish(
			chatId = chatId,
			title = title,
			corporate = corporate,
			rootId = rootId,
			personId = personId,
			bot = true,
			lines = listOf(
				ChatLine(
					id = "hello",
					mine = false,
					text = "",
					sentAt = Instant.now().toString(),
					author = title,
					authorId = "",
					replyAuthor = null,
					replyText = null,
					threadRoot = null,
					deleted = false,
					edited = false,
					service = false,
				),
			),
			members = 0,
			people = emptyList(),
			selfName = "",
			reactions = emptyMap(),
			reactionViewers = emptyMap(),
		)
	}

	private fun handleOpenChat(
		chatId: String,
		title: String,
		corporate: Boolean,
		rootId: String?,
		personId: String,
	) {
		val cached = readCachedMessagesUseCase(chatId)

		handlePublish(
			chatId = chatId,
			title = title,
			corporate = corporate,
			rootId = rootId,
			personId = personId,
			bot = false,
			lines = cached?.lines.orEmpty(),
			members = cached?.members ?: 0,
			people = cached?.people.orEmpty(),
			selfName = cached?.selfName.orEmpty(),
			reactions = reactionGlyphs(cached?.reactions.orEmpty()) + reactionGlyphs(readReactionsUseCase().chosen),
			reactionViewers = cached?.reactionViewers.orEmpty(),
		)

		handleListen(
			chatId = chatId,
			title = title,
			corporate = corporate,
			rootId = rootId,
			personId = personId,
		)

		handleLoadMessages(
			chatId = chatId,
			title = title,
			corporate = corporate,
			rootId = rootId,
			personId = personId,
		)
	}

	private fun handleListen(
		chatId: String,
		title: String,
		corporate: Boolean,
		rootId: String?,
		personId: String,
	) {
		realtime.ensureStarted()

		launchTry {
			realtime.chatIds.collect { changed ->
				if (changed == chatId) {
					handleLoadMessages(
						chatId = chatId,
						title = title,
						corporate = corporate,
						rootId = rootId,
						personId = personId,
					)
				}
			}
		} catch ::handleRefreshFailure
	}

	private fun handleChangeDraft(value: String) {
		handleDraft(value)
	}

	private fun handlePickMention(token: String) {
		val currentState = state.value as? ConversationState.Content ?: return

		val next = applyMentionUseCase(
			draft = currentState.draft,
			token = token,
		)

		handleDraft(next)
	}

	private fun handleBeginEdit(line: ChatLine) {
		if (!line.mine || line.deleted) {
			return
		}

		val currentState = state.value as? ConversationState.Content ?: return

		setState(
			currentState.copy(
				menuLineId = null,
				editingId = line.id,
				editingText = line.text,
				replyAuthor = "",
				replyText = "",
			),
		)

		handleDraft(line.text)
	}

	private fun handleCancelEdit() {
		val currentState = state.value as? ConversationState.Content ?: return

		setState(
			currentState.copy(
				editingId = null,
				editingText = "",
			),
		)

		handleDraft("")
	}

	private fun handleStartReply(author: String, text: String) {
		val currentState = state.value as? ConversationState.Content ?: return

		setState(
			currentState.copy(
				menuLineId = null,
				replyAuthor = author,
				replyText = text,
				editingId = null,
				editingText = "",
			)
		)
	}

	private fun handleCancelReply() {
		val currentState = state.value as? ConversationState.Content ?: return

		setState(currentState.copy(replyAuthor = "", replyText = ""))
	}

	private fun handleSend() {
		val currentState = state.value as? ConversationState.Content ?: return

		val text = currentState.draft.trim()

		if (text.isEmpty() || currentState.sending) {
			return
		}

		if (currentState.editingId != null) {
			handleApplyEdit(text)

			return
		}

		val replyAuthor = currentState.replyAuthor

		val replyText = currentState.replyText

		val threadRoot = currentState.rootId.orEmpty()

		val chatId = currentState.chatId

		if (currentState.bot) {
			handleSendBot(text, replyAuthor, replyText)

			return
		}

		setState(
			currentState.copy(
				sending = true,
				replyAuthor = "",
				replyText = "",
			),
		)

		handleDraft("")

		launchTry {
			sendMessageUseCase(
				chatId,
				text,
				replyAuthor,
				replyText,
				threadRoot,
			)

			val sent = state.value as? ConversationState.Content ?: return@launchTry

			setState(sent.copy(sending = false))

			handleLoadMessages(chatId)
		} catch ::handleSendFailure
	}

	private fun handleSendBot(text: String, replyAuthor: String, replyText: String) {
		val currentState = state.value as? ConversationState.Content ?: return

		val line = ChatLine(
			id = "${currentState.lines.size}",
			mine = true,
			text = text,
			sentAt = Instant.now().toString(),
			author = null,
			authorId = "",
			replyAuthor = replyAuthor.ifEmpty { null },
			replyText = replyText.ifEmpty { null },
			threadRoot = currentState.rootId,
			deleted = false,
			edited = false,
			service = false,
		)

		val lines = currentState.lines + line

		setState(
			currentState.copy(
				replyAuthor = "",
				replyText = "",
				lines = lines,
				days = lines.days(),
			),
		)

		handleDraft("")
	}

	private fun handleSyncTitles() {
		val current = state.value as? ConversationState.Content ?: return

		launchTry {
			setState(current.renamed(readChatsUseCase().savedTitle(current.chatId)) ?: return@launchTry)
		} catch ::handleRefreshFailure
	}

	private fun handleOpenPerson() {
		val currentState = state.value as? ConversationState.Content ?: return

		if (currentState.personId.isEmpty()) {
			if (!currentState.bot) {
				router.navigateToGroup(
					chatId = currentState.chatId,
					title = currentState.title,
					members = memberNames(currentState.people),
					corporate = currentState.corporate,
				)
			}

			return
		}

		router.navigateToPerson(
			userId = currentState.personId,
			corporate = currentState.corporate,
		)
	}

	private fun handleOpenMessage(lineId: String) {
		val currentState = state.value as? ConversationState.Content ?: return

		if (currentState.selectedIds.isNotEmpty()) {
			handleToggleSelection(lineId)

			return
		}

		setState(currentState.copy(menuLineId = lineId))
	}

	private fun handleDismissMessageMenu() {
		val currentState = state.value as? ConversationState.Content ?: return

		setState(currentState.copy(menuLineId = null))
	}

	private fun handlePickReaction(lineId: String, reaction: String) {
		val currentState = state.value as? ConversationState.Content ?: return

		val glyph = reactionGlyph(reaction)

		val chosen = currentState.reactions[lineId].orEmpty()

		val active = glyph !in chosen

		val next = if (active) {
			chosen + glyph
		} else {
			chosen - glyph
		}

		setState(
			currentState.copy(
				reactions = currentState.reactions + (lineId to next),
				reactionViewers = currentState.reactionViewers + (
					lineId to currentState.reactionViewers[lineId].orEmpty().mineGlyph(
						glyph = glyph,
						sentAt = Instant.now().toString(),
						selfName = currentState.selfName,
					)
				),
				usedReactions = currentState.usedReactions.usedGlyph(glyph = glyph, active = active),
				menuLineId = null,
			),
		)

		saveReactionUseCase(lineId, next)

		if (currentState.bot) {
			return
		}

		launchTry {
			reactUseCase(lineId, glyph, active)
		} catch ::handleRefreshFailure
	}

	private fun handlePin(lineId: String) {
		val currentState = state.value as? ConversationState.Content ?: return

		setState(
			currentState.copy(
				menuLineId = null,
				pinnedId = if (currentState.pinnedId == lineId) {
					null
				} else {
					lineId
				},
			)
		)
	}

	private fun handleStartDiscussion(lineId: String) {
		val currentState = state.value as? ConversationState.Content ?: return

		setState(currentState.copy(menuLineId = null))

		val screenId = if (currentState.bot) "bot:${currentState.chatId}" else currentState.chatId

		router.navigateToDiscussion(
			chatId = screenId,
			title = currentState.title,
			corporate = currentState.corporate,
			messageId = lineId,
			personId = currentState.personId,
		)
	}

	private fun handleStartSelection(lineId: String) {
		val currentState = state.value as? ConversationState.Content ?: return

		setState(currentState.copy(menuLineId = null, selectedIds = setOf(lineId)))
	}

	private fun handleToggleSelection(lineId: String) {
		val currentState = state.value as? ConversationState.Content ?: return

		val next = if (lineId in currentState.selectedIds) {
			currentState.selectedIds - lineId
		} else {
			currentState.selectedIds + lineId
		}

		setState(currentState.copy(selectedIds = next))
	}

	private fun handleClearSelection() {
		val currentState = state.value as? ConversationState.Content ?: return

		setState(currentState.copy(selectedIds = emptySet()))
	}

	private fun handleDeleteSelection() {
		val currentState = state.value as? ConversationState.Content ?: return

		val ids = currentState.selectedIds

		val lines = currentState.lines.map { line ->
			if (line.id in ids) {
				line.markDeleted()
			} else {
				line
			}
		}

		setState(
			currentState.copy(
				lines = lines,
				days = lines.days(),
				selectedIds = emptySet(),
				menuLineId = null,
				pinnedId = if (currentState.pinnedId in ids) {
					null
				} else {
					currentState.pinnedId
				},
				reactions = currentState.reactions.filterKeys { it !in ids },
				reactionViewers = currentState.reactionViewers.filterKeys { it !in ids },
			)
		)

		if (currentState.bot) {
			return
		}

		launchTry {
			ids.forEach { id -> deleteMessageUseCase(id) }
		} catch ::handleDeleteFailure
	}

	private fun handleApplyEdit(text: String) {
		val currentState = state.value as? ConversationState.Content ?: return

		val editing = currentState.editingId ?: return

		val lines = currentState.lines.map { line ->
			if (line.id == editing) {
				line.copy(text = text, edited = line.edited || text != line.text)
			} else {
				line
			}
		}

		setState(
			currentState.copy(
				editingId = null,
				editingText = "",
				lines = lines,
				days = lines.days(),
			),
		)

		handleDraft("")
	}

	private fun handleLoadMessages(chatId: String) {
		val current = state.value as? ConversationState.Content ?: return

		handleLoadMessages(
			chatId = chatId,
			title = current.title,
			corporate = current.corporate,
			rootId = current.rootId,
			personId = current.personId,
		)
	}

	private fun handleLoadMessages(
		chatId: String,
		title: String,
		corporate: Boolean,
		rootId: String?,
		personId: String,
	) {
		val failure = if (state.value is ConversationState.Content) ::handleRefreshFailure else ::handleLoadFailure

		launchTry {
			val thread = loadMessagesUseCase(chatId)

			val contentState = state.value as? ConversationState.Content

			handlePublish(
				chatId = contentState?.chatId ?: chatId,
				title = contentState?.title ?: title,
				corporate = contentState?.corporate ?: corporate,
				rootId = contentState?.rootId ?: rootId,
				personId = contentState?.personId ?: personId,
				bot = contentState?.bot ?: false,
				lines = thread.lines,
				members = thread.members,
				people = thread.people,
				selfName = thread.selfName,
				reactions = reactionGlyphs(thread.reactions) + reactionGlyphs(readReactionsUseCase().chosen),
				reactionViewers = thread.reactionViewers,
			)

			handleMarkRead(chatId, thread.readUpTo)
		} catch failure
	}

	private suspend fun handleMarkRead(chatId: String, messageId: String?) {
		if (markedChatId != chatId) {
			markedChatId = chatId

			markedMessageId = null
		}

		if (messageId == null || messageId == markedMessageId) {
			return
		}

		try {
			markReadUseCase(messageId)
		} catch (error: CancellationException) {
			throw error
		} catch (error: Exception) {
			handleRefreshFailure(error)
			return
		}

		markedMessageId = messageId
	}

	private fun handleSendFailure(error: Exception) {
		(state.value as? ConversationState.Content)?.let { current -> setState(current.copy(sending = false)) }

		handleRefreshFailure(error)
	}

	private fun handleDeleteFailure(error: Exception) {
		handleRefreshFailure(error)

		(state.value as? ConversationState.Content)?.let { current -> handleLoadMessages(current.chatId) }
	}

	private fun handleRefreshFailure(error: Exception) {
		val result = errorDelegate.processNonBlockingError(error)

		val recommended = (result as? NonBlockingErrorProcessResult.NotHandled)?.recommended ?: return

		setEvent(recommended.toEvent())
	}

	private fun handleLoadFailure(error: Exception) {
		val result = errorDelegate.processBlockingError(
			exception = error,
			retryIntent = ConversationIntent.NavigateBack,
			backIntent = ConversationIntent.NavigateBack,
		)

		if (result is BlockingErrorProcessResult.Handled) {
			return
		}

		setState(
			ConversationState.Error(message = error.userMessage())
		)
	}

	private fun DefaultErrorEvent.toEvent(): ConversationEvent =
		when (this) {
			DefaultErrorEvent.NoConnection -> ConversationEvent.NoConnection
			DefaultErrorEvent.Default -> ConversationEvent.Error
		}

	private fun handlePublish(
		chatId: String,
		title: String,
		corporate: Boolean,
		rootId: String?,
		personId: String,
		bot: Boolean,
		lines: List<ChatLine>,
		members: Int,
		people: List<ChatPerson>,
		selfName: String,
		reactions: Map<String, Set<String>>,
		reactionViewers: Map<String, List<ChatReaction>>,
	) {
		when (val current = state.value) {
			is ConversationState.Content -> {
				val kept = lines.keepingEdits(current.lines)

				setState(
					current.copy(
						lines = kept,
						days = kept.days(),
						members = members,
						people = people,
						selfName = selfName.ifBlank { current.selfName },
						reactions = reactions,
						reactionViewers = mergeReactionViewers(
							remote = reactionViewers,
							current = current.reactionViewers,
							selfName = selfName.ifBlank { current.selfName },
						),
					),
				)

				handleDraft(current.draft)
			}

			else                         -> setState(
				freshConversation(
					chatId = chatId,
					title = title,
					corporate = corporate,
					rootId = rootId,
					personId = personId,
					bot = bot,
					lines = lines,
					members = members,
					people = people,
					selfName = selfName,
					reactions = reactions,
					reactionViewers = reactionViewers,
					used = readReactionsUseCase().used,
				),
			)
		}
	}

	private fun handleDraft(value: String) {
		val current = state.value as? ConversationState.Content ?: return

		setState(
			current.drafted(
				value = value,
				mentionQueryUseCase = mentionQueryUseCase,
				matchMentionUseCase = matchMentionUseCase,
				everyoneMentionUseCase = everyoneMentionUseCase,
				initialsUseCase = initialsUseCase,
			),
		)
	}
}

private fun mergeReactionViewers(
	remote: Map<String, List<ChatReaction>>,
	current: Map<String, List<ChatReaction>>,
	selfName: String,
): Map<String, List<ChatReaction>> = (remote.keys + current.keys).associate { id ->
	val viewers = if (id !in current) {
		remote[id].orEmpty()
	} else {
		val mine = current.getValue(id).filter { viewer -> viewer.mine }
		val others = remote[id].orEmpty().filter { viewer -> !viewer.mine }
		others + mine
	}

	id to viewers.map { viewer ->
		if (selfName.isNotEmpty() && viewer.mine && viewer.name.isBlank()) {
			viewer.copy(name = selfName)
		} else {
			viewer
		}
	}
}

private fun ConversationState.Content.drafted(
	value: String,
	mentionQueryUseCase: MentionQueryUseCase,
	matchMentionUseCase: MatchMentionUseCase,
	everyoneMentionUseCase: EveryoneMentionUseCase,
	initialsUseCase: InitialsUseCase,
): ConversationState.Content {
	val query = mentionQueryUseCase(value)

	val mentions = if (query == null) {
		emptyList()
	} else {
		people.filter { person ->
			matchMentionUseCase(
				name = person.displayName,
				username = person.username,
				query = query,
			)
		}.map { person ->
			val title = person.displayName.ifBlank { person.username }
			person.toMention(initialsUseCase(title))
		}
	}

	return copy(
		draft = value,
		mentionQuery = query,
		mentions = mentions,
		mentionEveryone = query != null && everyoneMentionUseCase(query),
	)
}

private fun memberNames(people: List<ChatPerson>): List<String> =
	people.map { person -> person.displayName.ifBlank { person.username } }

private fun freshConversation(
	chatId: String,
	title: String,
	corporate: Boolean,
	rootId: String?,
	personId: String,
	bot: Boolean,
	lines: List<ChatLine>,
	members: Int,
	people: List<ChatPerson>,
	selfName: String,
	reactions: Map<String, Set<String>>,
	reactionViewers: Map<String, List<ChatReaction>>,
	used: List<String>,
): ConversationState.Content = ConversationState.Content(
	title = title,
	lines = lines,
	days = lines.days(),
	members = members,
	people = people,
	selfName = selfName,
	chatId = chatId,
	bot = bot,
	draft = "",
	mentionQuery = null,
	mentions = emptyList(),
	mentionEveryone = false,
	editingId = null,
	editingText = "",
	sending = false,
	menuLineId = null,
	pinnedId = null,
	selectedIds = emptySet(),
	reactions = reactions,
	reactionViewers = reactionViewers,
	usedReactions = used,
	corporate = corporate,
	replyAuthor = "",
	replyText = "",
	rootId = rootId,
	personId = personId,
)

private fun List<Chat>?.savedTitle(chatId: String): String? =
	this?.firstOrNull { chat -> chat.id == chatId }?.title?.takeIf { title -> title.isNotEmpty() }

private fun ConversationState.Content.renamed(title: String?): ConversationState.Content? {
	if (title == null || title == this.title) {
		return null
	}

	return copy(title = title)
}