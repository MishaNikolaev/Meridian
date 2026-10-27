package com.nmichail.meridian.features.chat.presentation

import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.entity.ChatDiscussion

enum class ChatSection {
	ALL,
	CATALOG,
	BOTS,
	DISCUSSIONS,
}

sealed interface ChatState {

	data object Initial : ChatState

	data object Loading : ChatState

	data class Content(
		val corporate: Boolean,
		val section: ChatSection,
		val corporateChats: List<Chat>,
		val externalTabChats: List<Chat>,
		val corporateDiscussions: List<ChatDiscussion>,
		val externalTabDiscussions: List<ChatDiscussion>,
		val createOpen: Boolean,
	) : ChatState

	data object Error : ChatState
}