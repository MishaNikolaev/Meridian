package com.nmichail.meridian.shared.phone.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateCallDto(
	val chatId: String,
	val media: String,
)