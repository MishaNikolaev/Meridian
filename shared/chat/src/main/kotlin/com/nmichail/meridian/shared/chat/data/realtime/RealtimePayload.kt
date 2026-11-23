package com.nmichail.meridian.shared.chat.data.realtime

import kotlinx.serialization.Serializable

@Serializable
data class RealtimePayload(
	val chatId: String? = null,
	val callId: String? = null,
	val state: String? = null,
	val actorUserId: String? = null,
	val media: String? = null,
)