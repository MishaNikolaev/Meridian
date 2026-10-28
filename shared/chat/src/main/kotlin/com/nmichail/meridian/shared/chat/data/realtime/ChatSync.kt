package com.nmichail.meridian.shared.chat.data.realtime

import kotlinx.serialization.Serializable

@Serializable
internal data class ChatSync(
	val event: RealtimeEvent,
)