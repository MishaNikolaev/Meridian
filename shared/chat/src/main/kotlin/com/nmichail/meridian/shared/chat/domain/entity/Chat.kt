package com.nmichail.meridian.shared.chat.domain.entity

enum class ChatType {
	DIRECT,
	GROUP,
	CHANNEL,
}

data class ChatSummary(
	val id: String,
	val type: ChatType,
	val title: String,
	val preview: String,
	val sentAt: String?,
	val unread: Int,
)

data class ChatLine(
	val id: String,
	val mine: Boolean,
	val text: String,
	val sentAt: String?,
	val author: String? = null,
)

data class ChatThread(
	val lines: List<ChatLine>,
	val members: Int,
	val readUpTo: String? = null,
)

data class CreatedChat(
	val id: String,
	val title: String,
)
