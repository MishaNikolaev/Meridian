package com.nmichail.meridian.shared.chat.data.realtime

import kotlinx.serialization.Serializable

@Serializable
data class RealtimePayload(
	val chatId: String? = null,
)