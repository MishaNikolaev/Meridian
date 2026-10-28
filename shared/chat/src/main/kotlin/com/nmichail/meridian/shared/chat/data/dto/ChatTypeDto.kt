package com.nmichail.meridian.shared.chat.data.dto

import kotlinx.serialization.Serializable

@Serializable
enum class ChatTypeDto {
	DIRECT,
	GROUP,
	CHANNEL,
}