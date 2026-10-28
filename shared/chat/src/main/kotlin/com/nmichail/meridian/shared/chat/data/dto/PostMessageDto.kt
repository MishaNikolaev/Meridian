package com.nmichail.meridian.shared.chat.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PostMessageDto(
	val clientMessageId: String,
	val deviceMessages: List<DeviceMessageDto>,
	val threadRoot: String?,
)