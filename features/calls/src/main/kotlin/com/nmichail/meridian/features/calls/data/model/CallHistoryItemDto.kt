package com.nmichail.meridian.features.calls.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CallHistoryItemDto(
	val callId: String,
	val chatId: String,
	val initiatorUserId: String,
	val media: String,
	val state: String,
	val startedAt: String,
	val endedAt: String?,
)