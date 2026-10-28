package com.nmichail.meridian.shared.chat.domain.entity

data class ChatThread(
	val lines: List<ChatLine>,
	val members: Int,
	val readUpTo: String?,
	val reactions: Map<String, String>,
	val reactionViewers: Map<String, List<ChatReaction>>,
	val people: List<ChatPerson>,
)