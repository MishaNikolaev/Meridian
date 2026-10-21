package com.nmichail.meridian.shared.chat.data.model

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

@Serializable
data class RealtimeEvent(
	val id: Long,
	val type: String,
	val payload: RealtimePayload,
	val createdAt: String,
)

@Serializable
data class RealtimePayload(
	val chatId: String? = null,
)
