package com.nmichail.meridian.shared.chat.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateChatDto(
	val type: ChatTypeDto,
	val title: String?,
	val memberIds: List<String>,
)