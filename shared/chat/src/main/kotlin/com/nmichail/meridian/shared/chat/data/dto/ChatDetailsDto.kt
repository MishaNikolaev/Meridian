package com.nmichail.meridian.shared.chat.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatDetailsDto(
	val chat: ChatDto,
	val members: List<ChatMemberDto>,
)