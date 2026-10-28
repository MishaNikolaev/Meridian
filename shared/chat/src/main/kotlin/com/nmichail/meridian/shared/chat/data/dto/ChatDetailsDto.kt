package com.nmichail.meridian.shared.chat.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatDetailsDto(
	val chat: ChatSummaryDto,
	val members: List<ChatMemberDto>,
)