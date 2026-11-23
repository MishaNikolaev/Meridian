package com.nmichail.meridian.shared.chat.domain.entity

data class RingNotice(
	val callId: String,
	val chatId: String,
	val state: String,
	val actorUserId: String,
	val media: String,
)