package com.nmichail.meridian.shared.chat.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatPageDto(
	val items: List<ChatSummaryDto>,
)