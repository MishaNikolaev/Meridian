package com.nmichail.meridian.shared.chat.domain.entity

data class ChatProfile(
	val title: String,
	val description: String,
	val owner: String,
	val members: List<ChatPerson>,
	val selfId: String,
)