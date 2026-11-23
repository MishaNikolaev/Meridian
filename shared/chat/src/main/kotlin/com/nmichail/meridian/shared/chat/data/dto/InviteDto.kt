package com.nmichail.meridian.shared.chat.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class InviteDto(
	val code: String,
	val chatId: String,
	val expiresAt: String,
)