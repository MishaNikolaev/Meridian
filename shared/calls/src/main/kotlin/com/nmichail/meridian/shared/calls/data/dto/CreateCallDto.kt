package com.nmichail.meridian.shared.calls.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateCallDto(
	val chatId: String,
	val media: String,
)