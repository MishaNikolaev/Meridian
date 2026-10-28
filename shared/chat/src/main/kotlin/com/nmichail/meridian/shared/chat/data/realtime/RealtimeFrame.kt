package com.nmichail.meridian.shared.chat.data.realtime

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface RealtimeFrame {

	@Serializable
	@SerialName("ready")
	data object Ready : RealtimeFrame

	@Serializable
	@SerialName("typing")
	data object Typing : RealtimeFrame

	@Serializable
	@SerialName("call.signal")
	data object CallSignal : RealtimeFrame

	@Serializable
	@SerialName("sync")
	data class Sync(
		val event: RealtimeEvent,
	) : RealtimeFrame
}