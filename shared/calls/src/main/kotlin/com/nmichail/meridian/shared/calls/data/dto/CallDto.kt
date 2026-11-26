package com.nmichail.meridian.shared.calls.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CallDto(
	val callId: String,
	val chatId: String,
	val initiatorUserId: String,
	val state: String,
)
