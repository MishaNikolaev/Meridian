package com.nmichail.meridian.features.chat.presentation

import com.nmichail.meridian.shared.chat.domain.entity.ChatSummary

sealed interface ChatIntent {

	data object LoadInitialData : ChatIntent

	data class ChangeCorporate(
		val corporate: Boolean,
	) : ChatIntent

	data class ChangeSection(
		val section: ChatSection,
	) : ChatIntent

	data object ChangeSearchOpen : ChatIntent

	data class ChangeSearch(
		val search: String,
	) : ChatIntent

	data class ChangeCreate(
		val open: Boolean,
	) : ChatIntent

	data class ChangeTitle(
		val title: String,
	) : ChatIntent

	data class ChangeUsername(
		val username: String,
	) : ChatIntent

	data class OpenChat(
		val chat: ChatSummary,
	) : ChatIntent

	data class OpenBot(
		val id: String,
		val title: String,
	) : ChatIntent

	data object Submit : ChatIntent

	data object NavigateBack : ChatIntent
}
