package com.nmichail.meridian.shared.chat.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatSummaryDto(
	val chatId: String,
	val type: ChatTypeDto,
	val title: String?,
	val peerUserId: String?,
	val unreadCount: Int,
	val lastMessage: MessageDto?,
	val corporate: Boolean,
)