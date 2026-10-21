package com.nmichail.meridian.features.chat.presentation

import com.nmichail.meridian.core.validation.displayName.DisplayNameValidationItem
import com.nmichail.meridian.core.validation.username.UsernameValidationItem
import com.nmichail.meridian.shared.chat.domain.entity.ChatSummary

enum class ChatSection {
	ALL,
	WORK,
	PERSONAL,
	BOTS,
}

sealed interface ChatState {

	data object Initial : ChatState

	data object Loading : ChatState

	data class Content(
		val corporate: Boolean,
		val section: ChatSection,
		val chats: List<ChatSummary>,
		val search: String,
		val searchOpen: Boolean,
		val createOpen: Boolean,
		val title: DisplayNameValidationItem,
		val username: UsernameValidationItem,
		val creating: Boolean,
	) : ChatState

	data class Error(
		val message: Int,
	) : ChatState
}
