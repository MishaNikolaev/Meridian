package com.nmichail.meridian.shared.chat.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatDto(
	val chatId: String,
	val type: ChatTypeDto,
	val title: String?,
	val personId: String?,
	val personName: String? = null,
	val unreadCount: Int,
	val lastMessage: MessageDto?,
	val corporate: Boolean,
)
