package com.nmichail.meridian.shared.chat.domain.entity

data class ChatPerson(
	val userId: String,
	val username: String,
	val displayName: String,
	val role: String,
)