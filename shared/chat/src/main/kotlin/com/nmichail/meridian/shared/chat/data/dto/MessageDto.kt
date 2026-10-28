package com.nmichail.meridian.shared.chat.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
	val messageId: String,
	val chatId: String,
	val senderUserId: String,
	val createdAt: String,
	val deleted: Boolean,
	val deviceMessages: List<DeviceMessageDto>,
	val reactions: List<ReactionDto>,
	val threadRoot: String? = null,
)
