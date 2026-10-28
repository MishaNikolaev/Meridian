package com.nmichail.meridian.shared.chat.data.realtime

import kotlinx.serialization.Serializable

@Serializable
data class RealtimeEvent(
	val id: Long,
	val type: String,
	val payload: RealtimePayload,
	val createdAt: String,
)