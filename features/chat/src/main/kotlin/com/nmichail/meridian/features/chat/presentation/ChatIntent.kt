package com.nmichail.meridian.features.chat.presentation

import com.nmichail.meridian.shared.chat.domain.entity.Chat

sealed interface ChatIntent {

	data object LoadInitialData : ChatIntent

	data class ChangeCorporate(
		val corporate: Boolean,
	) : ChatIntent

	data class ChangeSection(
		val section: ChatSection,
	) : ChatIntent

	data object OpenSearch : ChatIntent

	data class ChangeCreate(
		val open: Boolean,
	) : ChatIntent

	data class OpenChat(
		val chat: Chat,
	) : ChatIntent

	data class OpenDiscussion(
		val chatId: String,
		val messageId: String,
		val title: String,
		val corporate: Boolean,
		val personId: String,
	) : ChatIntent

	data class OpenBot(
		val id: String,
		val title: String,
	) : ChatIntent

	data object OpenGroup : ChatIntent

	data object Retry : ChatIntent

	data object ChangeServer : ChatIntent

	data object NavigateBack : ChatIntent
}