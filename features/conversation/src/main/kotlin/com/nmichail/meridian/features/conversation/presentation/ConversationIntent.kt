package com.nmichail.meridian.features.conversation.presentation

import com.nmichail.meridian.shared.chat.domain.entity.ChatLine

sealed interface ConversationIntent {
	data class DraftFile(
		val name: String,
		val type: String,
		val bytes: ByteArray,
		val document: Boolean = false,
	)

	data class LoadInitialData(
		val chatId: String,
		val title: String,
		val corporate: Boolean,
		val rootId: String?,
		val personId: String,
	) : ConversationIntent

	data class ChangeDraft(
		val draft: String,
	) : ConversationIntent

	data class PickMention(
		val token: String,
	) : ConversationIntent

	data class BeginEdit(
		val line: ChatLine,
	) : ConversationIntent

	data object CancelEdit : ConversationIntent

	data class StartReply(
		val author: String,
		val text: String,
	) : ConversationIntent

	data object CancelReply : ConversationIntent

	data class AttachFile(
		val name: String,
		val type: String,
		val bytes: ByteArray,
		val document: Boolean = false,
	) : ConversationIntent

	data class AttachPhotos(
		val files: List<DraftFile>,
	) : ConversationIntent

	data class SendPoll(
		val question: String,
		val description: String,
		val options: List<String>,
		val correct: Int,
		val solution: String,
	) : ConversationIntent

	data class Vote(
		val pollId: String,
		val option: Int,
		val question: String,
	) : ConversationIntent

	data object ClearFile : ConversationIntent

	data class OpenFile(
		val fileId: String,
		val fileName: String,
		val fileType: String,
	) : ConversationIntent

	data class PlayFile(
		val fileId: String,
		val fileName: String,
		val fileType: String,
	) : ConversationIntent

	data object Send : ConversationIntent

	data object NavigateBack : ConversationIntent

	data object SyncTitles : ConversationIntent

	data object OpenPerson : ConversationIntent

	data class OpenMessage(
		val lineId: String,
	) : ConversationIntent

	data object DismissMessageMenu : ConversationIntent

	data class PickReaction(
		val lineId: String,
		val reaction: String,
	) : ConversationIntent

	data class Pin(
		val lineId: String,
	) : ConversationIntent

	data class StartDiscussion(
		val lineId: String,
	) : ConversationIntent

	data class StartSelection(
		val lineId: String,
	) : ConversationIntent

	data object ClearSelection : ConversationIntent

	data object DeleteSelection : ConversationIntent
}