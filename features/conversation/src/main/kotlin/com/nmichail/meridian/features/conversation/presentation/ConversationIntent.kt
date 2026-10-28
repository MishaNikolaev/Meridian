package com.nmichail.meridian.features.conversation.presentation

import com.nmichail.meridian.shared.chat.domain.entity.ChatLine

sealed interface ConversationIntent {

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

	data object Send : ConversationIntent

	data object NavigateBack : ConversationIntent

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