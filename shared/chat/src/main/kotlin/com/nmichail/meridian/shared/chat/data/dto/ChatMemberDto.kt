package com.nmichail.meridian.shared.chat.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatMemberDto(
	val userId: String,
	val username: String,
	val displayName: String,
)