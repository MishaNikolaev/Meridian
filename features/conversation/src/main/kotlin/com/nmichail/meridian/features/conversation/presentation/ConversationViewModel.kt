package com.nmichail.meridian.features.conversation.presentation

import com.nmichail.meridian.component.photopicker.domain.usecase.DownloadPhotoUseCase
import com.nmichail.meridian.component.photopicker.domain.usecase.NoteFacesUseCase
import com.nmichail.meridian.component.photopicker.domain.usecase.UploadPhotoUseCase
import com.nmichail.meridian.core.network.error.presentation.DefaultErrorEvent
import com.nmichail.meridian.core.network.error.presentation.ErrorDelegate
import com.nmichail.meridian.core.network.error.presentation.NonBlockingErrorProcessResult
import com.nmichail.meridian.core.presentation.BaseViewModel
import com.nmichail.meridian.core.validation.initials.InitialsUseCase
import com.nmichail.meridian.core.validation.shortname.ShortNameUseCase
import com.nmichail.meridian.core.validation.mention.ApplyMentionUseCase
import com.nmichail.meridian.core.validation.mention.EveryoneMentionUseCase
import com.nmichail.meridian.core.validation.mention.MatchMentionUseCase
import com.nmichail.meridian.core.validation.mention.MentionQueryUseCase
import com.nmichail.meridian.shared.chat.data.mapper.markedFile
import com.nmichail.meridian.shared.chat.data.mapper.mediaType
import com.nmichail.meridian.features.conversation.presentation.compose.component.days
import com.nmichail.meridian.features.conversation.presentation.compose.message.faceIds
import com.nmichail.meridian.features.conversation.presentation.compose.poll.note
import com.nmichail.meridian.shared.chat.domain.entity.ChatNote
import com.nmichail.meridian.shared.chat.domain.entity.Poll
import com.nmichail.meridian.shared.chat.domain.entity.Vote
import com.nmichail.meridian.features.conversation.presentation.compose.component.keepingEdits
import com.nmichail.meridian.features.conversation.presentation.compose.component.markDeleted
import com.nmichail.meridian.features.conversation.presentation.compose.message.mergeViewers
import com.nmichail.meridian.features.conversation.presentation.compose.message.mineReaction
import com.nmichail.meridian.features.conversation.presentation.compose.message.reactionEmoji
import com.nmichail.meridian.features.conversation.presentation.compose.message.reactionEmojis
import com.nmichail.meridian.features.conversation.presentation.compose.message.usedReaction
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine
import com.nmichail.meridian.shared.chat.domain.entity.ChatPerson
import com.nmichail.meridian.shared.chat.domain.entity.ChatThread
import com.nmichail.meridian.shared.chat.domain.realtime.ChatRealtime
import com.nmichail.meridian.shared.chat.domain.usecase.DeleteMessageUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.LoadMessagesUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.ReadChatsUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.ReadCachedMessagesUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.MarkReadUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.ReactUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.ReadReactionsUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.SaveReactionUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.SendMessageUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.SendNoteUseCase
import com.nmichail.meridian.shared.session.domain.usecase.GetUserIdUseCase
import java.time.Instant
import javax.inject.Inject

class ConversationViewModel @Inject constructor(
	private val loadMessagesUseCase: LoadMessagesUseCase,
	private val readCachedMessagesUseCase: ReadCachedMessagesUseCase,
	private val readChatsUseCase: ReadChatsUseCase,
	private val sendMessageUseCase: SendMessageUseCase,
	private val sendNoteUseCase: SendNoteUseCase,
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
	private val shortNameUseCase: ShortNameUseCase,
	private val realtime: ChatRealtime,
	private val errorDelegate: ErrorDelegate,
	private val router: ConversationRouter,
	private val getUserIdUseCase: GetUserIdUseCase,
	private val uploadPhotoUseCase: UploadPhotoUseCase,
	private val downloadPhotoUseCase: DownloadPhotoUseCase,
	private val noteFacesUseCase: NoteFacesUseCase,
) : BaseViewModel<ConversationState, ConversationIntent, ConversationEvent>() {

	override fun initState(): ConversationState = ConversationState.Initial

	override fun applyIntent(intent: ConversationIntent) {
		when (intent) {
			is ConversationIntent.LoadInitialData    -> handleLoadInitialData(intent)
			is ConversationIntent.ChangeDraft        -> handleChangeDraft(intent.draft)
			is ConversationIntent.PickMention        -> handlePickMention(intent.token)
			is ConversationIntent.BeginEdit          -> handleBeginEdit(intent.message)
			is ConversationIntent.CancelEdit         -> handleCancelEdit()
			is ConversationIntent.StartReply         -> handleStartReply(intent.author, intent.text)
			is ConversationIntent.CancelReply        -> handleCancelReply()
			is ConversationIntent.AttachFile         -> handleAttach(intent.name, intent.type, intent.bytes, intent.document)
			is ConversationIntent.AttachPhotos       -> handleAttachPhotos(intent.files)
			is ConversationIntent.SendPoll           -> handleSendNote(
				ChatNote.PollBody(
					Poll(
						question = intent.question,
						description = intent.description,
						options = intent.options,
						correct = intent.correct,
						solution = intent.solution,
						anonymous = intent.anonymous,
					),
				),
			)
			is ConversationIntent.Vote               -> handleSendNote(
				ChatNote.VoteBody(
					Vote(
						pollId = intent.pollId,
						option = intent.option,
						question = intent.question,
					),
				),
			)
			is ConversationIntent.ClearFile          -> handleClearFile()
			is ConversationIntent.OpenFile           -> handleOpenFile(intent.fileId, intent.fileName, intent.fileType)
			is ConversationIntent.PlayFile           -> handleDownloadFile(intent.fileId, intent.fileName, intent.fileType)
			is ConversationIntent.Send               -> handleSendDraft()
			is ConversationIntent.NavigateBack       -> handleNavigateBack()
			is ConversationIntent.SyncTitles         -> handleSyncTitles()
			is ConversationIntent.OpenPerson         -> handleOpenPerson()
			is ConversationIntent.StartCall          -> handleStartCall()
			is ConversationIntent.OpenLink          -> handleOpenLink(intent.url)
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

		val lines = emptyList<ChatLine>()

		setState(
			ConversationState.Content(
				title = load.title,
				lines = lines,
				days = lines.days(),
				members = 0,
				people = emptyList(),
				selfName = "",
				chatId = load.chatId.removePrefix("bot:"),
				bot = load.chatId.startsWith("bot:"),
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
				reactions = emptyMap(),
				reactionViewers = emptyMap(),
				usedReactions = emptyList(),
				corporate = load.corporate,
				replyAuthor = "",
				replyName = "",
				replyText = "",
				rootId = load.rootId,
				personId = load.personId,
				files = emptyList(),
				fileName = "",
				previews = emptyList(),
				photo = "",
			),
		)

		val current = state.value as? ConversationState.Content ?: return

		if (current.bot) {
			handleOpenBot()
		} else {
			handleOpenChat()
		}
	}

	private fun handleOpenBot() {
		val current = state.value as? ConversationState.Content ?: return

		handlePublish(
			ChatThread(
				lines = listOf(
					ChatLine(
						id = "hello",
						mine = false,
						text = "",
						sentAt = Instant.now().toString(),
						author = current.title,
						authorId = "",
						replyAuthor = null,
						replyText = null,
						threadRoot = null,
						deleted = false,
						edited = false,
						service = false,
						fileId = "",
						fileName = "",
						fileType = "",
						poll = null,
						vote = null,
					),
				),
				members = 0,
				readUpTo = null,
				reactions = emptyMap(),
				reactionViewers = emptyMap(),
				people = emptyList(),
				selfName = "",
			),
		)
	}

	private fun handleOpenChat() {
		val current = state.value as? ConversationState.Content ?: return

		val thread = readCachedMessagesUseCase(current.chatId)
			?: ChatThread(
				lines = emptyList(),
				members = 0,
				readUpTo = null,
				reactions = emptyMap(),
				reactionViewers = emptyMap(),
				people = emptyList(),
				selfName = "",
			)

		handlePublish(thread)
		handleListen()
		handleLoadMessages()
	}

	private fun handleListen() {
		realtime.ensureStarted()

		launchTry {
			realtime.chatIds.collect { changed ->
				val current = state.value as? ConversationState.Content ?: return@collect

				if (changed == current.chatId) {
					handleLoadMessages()
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

	private fun handleBeginEdit(message: ChatLine) {
		if (!message.mine || message.deleted) {
			return
		}

		val currentState = state.value as? ConversationState.Content ?: return

		setState(
			currentState.copy(
				menuLineId = null,
				editingId = message.id,
				editingText = message.text,
				replyAuthor = "",
				replyName = "",
				replyText = "",
			),
		)

		handleDraft(message.text)
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
				replyName = shortNameUseCase(author),
				replyText = text,
				editingId = null,
				editingText = "",
			)
		)
	}

	private fun handleCancelReply() {
		val current = state.value as? ConversationState.Content ?: return

		setState(current.copy(replyAuthor = "", replyName = "", replyText = ""))
	}

	private fun handleAttach(name: String, type: String, bytes: ByteArray, document: Boolean) {
		val currentState = state.value as? ConversationState.Content ?: return
		val file = DraftFile(name = name, type = type, bytes = bytes, document = document)

		if (!file.accepted()) {
			setEvent(ConversationEvent.FileRejected)

			return
		}

		val files = listOf(file)
		setState(currentState.copy(files = files, fileName = files.attachedName(), previews = files.previews()))
	}

	private fun handleAttachPhotos(picked: List<DraftFile>) {
		val currentState = state.value as? ConversationState.Content ?: return
		val accepted = picked.filter { file -> file.accepted() }.take(n = 10)

		if (accepted.isEmpty()) {
			setEvent(ConversationEvent.FileRejected)

			return
		}

		setState(currentState.copy(files = accepted, fileName = accepted.attachedName(), previews = accepted.previews()))

		if (accepted.size < picked.size) {
			setEvent(ConversationEvent.FileRejected)
		}
	}

	private fun handleClearFile() {
		val current = state.value as? ConversationState.Content ?: return

		setState(current.copy(files = emptyList(), fileName = "", previews = emptyList()))
	}

	private fun handleOpenFile(fileId: String, fileName: String, fileType: String) {
		val current = state.value as? ConversationState.Content
		val kind = fileType.mediaType()
		val picture = kind.startsWith("image/") || kind.startsWith("video/")

		if (current != null && picture) {
			setState(current.copy(photo = fileId))
		} else {
			handleDownloadFile(fileId, fileName, fileType)
		}
	}

	private fun handleOpenLink(url: String) {
		val code = conferenceCode(url) ?: return
		router.navigateToConference(code)
	}

	private fun handleStartCall() {
		val current = state.value as? ConversationState.Content ?: return
		if (current.bot) {
			return
		}
		val currentUserId = getUserIdUseCase().orEmpty()
		if (currentUserId.isNotEmpty() && current.personId == currentUserId) {
			setEvent(ConversationEvent.CannotCallSelf)
			return
		}
		router.navigateToCall(current.chatId, current.title, current.personId, video = false)
	}

	private fun handleNavigateBack() {
		val current = state.value as? ConversationState.Content

		if (current != null && current.photo.isNotEmpty()) {
			setState(current.copy(photo = ""))
		} else {
			router.navigateBack()
		}
	}

	private fun handleDownloadFile(fileId: String, fileName: String, fileType: String) = launchTry {
		setEvent(ConversationEvent.OpenedFile(fileName, fileType, downloadPhotoUseCase(fileId), fileId))
	} catch ::handleRefreshFailure

	private fun handleSendDraft() {
		val currentState = state.value as? ConversationState.Content ?: return

		val text = currentState.draft.trim().ifEmpty { currentState.files.spokenName() }

		if (text.isEmpty() || currentState.sending) {
			return
		}

		if (currentState.editingId != null) {
			handleApplyEdit(text)

			return
		}

		if (currentState.bot) {
			handleSendBot(text, currentState.replyAuthor, currentState.replyText)

			return
		}

		handleSendMessage(currentState, text)
	}

	private fun handleSendMessage(currentState: ConversationState.Content, text: String) {
		val files = currentState.files

		setState(currentState.copy(sending = true, replyAuthor = "", replyName = "", replyText = "", fileName = "", previews = emptyList()))
		handleDraft("")

		launchTry {
			handleUpload(currentState, text, files)

			val sent = state.value as? ConversationState.Content ?: return@launchTry

			setState(sent.copy(sending = false, files = emptyList()))
			handleLoadMessages()
		} catch { error ->
			(state.value as? ConversationState.Content)?.let { shown ->
				setState(shown.copy(fileName = shown.files.attachedName(), previews = shown.files.previews()))
			}
			handleSendFailure(error)
		}
	}

	private suspend fun handleUpload(currentState: ConversationState.Content, text: String, files: List<DraftFile>) {
		val chatId = currentState.chatId
		val replyAuthor = currentState.replyAuthor
		val replyText = currentState.replyText
		val threadRoot = currentState.rootId.orEmpty()

		if (files.isEmpty()) {
			sendMessageUseCase(
				chatId,
				text,
				replyAuthor,
				replyText,
				threadRoot,
				"",
				"",
				"",
				emptyList(),
			)

			return
		}

		files.forEachIndexed { index, file ->
			val photoId = uploadPhotoUseCase(file.bytes, file.type)
			val caption = if (index == 0) text.ifEmpty { file.name } else file.name
			val stored = if (file.document) file.type.markedFile(file.bytes.size) else file.type

			sendMessageUseCase(
				chatId,
				caption,
				if (index == 0) replyAuthor else "",
				if (index == 0) replyText else "",
				threadRoot,
				photoId,
				file.name,
				stored,
				listOf(photoId),
			)
		}
	}

	private fun handleSendBot(text: String, replyAuthor: String, replyText: String) {
		val currentState = state.value as? ConversationState.Content ?: return
		val lines = currentState.lines + ChatLine(
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
			fileId = "",
			fileName = "",
			fileType = "",
			poll = null,
			vote = null,
		)

		setState(
			currentState.copy(
				replyAuthor = "",
				replyName = "",
				replyText = "",
				lines = lines,
				days = lines.days(),
			),
		)

		handleDraft("")
	}

	private fun handleSyncTitles() {
		if (state.value !is ConversationState.Content) {
			return
		}

		launchTry {
			val chatId = (state.value as? ConversationState.Content)?.chatId ?: return@launchTry
			val next = readChatsUseCase()?.firstOrNull { chat -> chat.id == chatId }?.title
			val current = state.value as? ConversationState.Content ?: return@launchTry

			if (next.isNullOrEmpty() || next == current.title) {
				return@launchTry
			}

			setState(current.copy(title = next))
		} catch ::handleRefreshFailure
	}

	private fun handleOpenPerson() {
		val current = state.value as? ConversationState.Content ?: return

		if (current.personId.isEmpty()) {
			if (!current.bot) {
				router.navigateToGroup(
					chatId = current.chatId,
					title = current.title,
					members = current.people.map { person -> person.displayName.ifBlank { person.username } },
					corporate = current.corporate,
				)
			}

			return
		}

		router.navigateToPerson(userId = current.personId, corporate = current.corporate)
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

		val emoji = reactionEmoji(reaction)
		val chosen = currentState.reactions[lineId].orEmpty()
		val active = emoji !in chosen
		val next = if (active) {
			chosen + emoji
		} else {
			chosen - emoji
		}

		setState(
			currentState.copy(
				reactions = currentState.reactions + (lineId to next),
				reactionViewers = currentState.reactionViewers + (
					lineId to currentState.reactionViewers[lineId].orEmpty().mineReaction(
						emoji = emoji,
						sentAt = Instant.now().toString(),
						selfName = currentState.selfName,
					)
					),
				usedReactions = currentState.usedReactions.usedReaction(emoji = emoji, active = active),
				menuLineId = null,
			),
		)

		saveReactionUseCase(lineId, next)

		if (currentState.bot) {
			return
		}

		launchTry {
			reactUseCase(lineId, emoji, active)
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

	private fun handleLoadMessages() {
		val current = state.value as? ConversationState.Content ?: return

		launchTry {
			val thread = loadMessagesUseCase(current.chatId)

			handlePublish(thread)
			handleMarkRead(thread.readUpTo)
		} catch ::handleRefreshFailure
	}

	private fun handleMarkRead(messageId: String?) {
		if (messageId == null) {
			return
		}

		launchTry {
			markReadUseCase(messageId)
		} catch ::handleRefreshFailure
	}

	private fun handleSendNote(note: ChatNote) = note(
		current = state.value as? ConversationState.Content,
		note = note,
		send = sendNoteUseCase,
		read = { state.value },
		publish = ::setState,
		reload = { handleLoadMessages() },
	) { block ->
		launchTry(block) catch ::handleSendFailure
	}

	private fun handleSendFailure(error: Exception) {
		(state.value as? ConversationState.Content)?.let { current -> setState(current.copy(sending = false)) }

		handleRefreshFailure(error)
	}

	private fun handleDeleteFailure(error: Exception) {
		handleRefreshFailure(error)

		(state.value as? ConversationState.Content)?.let { handleLoadMessages() }
	}

	private fun handleRefreshFailure(error: Exception) {
		val result = errorDelegate.processNonBlockingError(error)

		val recommended = (result as? NonBlockingErrorProcessResult.NotHandled)?.recommended ?: return

		setEvent(recommended.toEvent())
	}

	private fun DefaultErrorEvent.toEvent(): ConversationEvent =
		when (this) {
			DefaultErrorEvent.NoConnection -> ConversationEvent.NoConnection
			DefaultErrorEvent.Default      -> ConversationEvent.Error
		}

	private fun handlePublish(thread: ChatThread) {
		val current = state.value as? ConversationState.Content ?: return
		val memory = readReactionsUseCase()
		val reactions = reactionEmojis(thread.reactions) + reactionEmojis(memory.chosen)
		val lines = thread.lines.keepingEdits(current.lines)
		val selfName = thread.selfName.ifBlank { current.selfName }

		setState(
			current.copy(
				lines = lines,
				days = lines.days(),
				members = thread.members,
				people = thread.people,
				selfName = selfName,
				reactions = reactions,
				reactionViewers = thread.reactionViewers.mergeViewers(current.reactionViewers, selfName),
				usedReactions = current.usedReactions.ifEmpty { memory.used },
			),
		)
		handleDraft(current.draft)

		launchTry { noteFacesUseCase(thread.faceIds()) } catch ::handleRefreshFailure
	}

	private fun handleMention(person: ChatPerson): Mention {
		val title = person.displayName.ifBlank { person.username }

		return Mention(
			token = person.username,
			title = title,
			note = "@${person.username}",
			initials = initialsUseCase(title),
			userId = person.userId,
		)
	}

	private fun handleDraft(value: String) {
		val current = state.value as? ConversationState.Content ?: return
		val query = mentionQueryUseCase(value)
		val mentions = if (query == null) {
			emptyList()
		} else {
			current.people.filter { person ->
				matchMentionUseCase(name = person.displayName, username = person.username, query = query)
			}.map(::handleMention)
		}

		setState(
			current.copy(
				draft = value,
				mentionQuery = query,
				mentions = mentions,
				mentionEveryone = query != null && everyoneMentionUseCase(query),
			),
		)
	}
}