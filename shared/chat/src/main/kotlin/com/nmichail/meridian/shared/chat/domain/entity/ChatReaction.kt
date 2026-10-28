package com.nmichail.meridian.shared.chat.domain.entity

data class ChatReaction(
	val name: String,
	val reaction: String,
	val sentAt: String?,
	val mine: Boolean,
)