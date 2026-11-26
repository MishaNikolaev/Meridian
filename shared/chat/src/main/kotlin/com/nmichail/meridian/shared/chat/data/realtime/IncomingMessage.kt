package com.nmichail.meridian.shared.chat.data.realtime

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface IncomingMessage {
	@Serializable
	@SerialName("ready")
	data class Ready(
		val userId: String,
		val deviceId: String,
	) : IncomingMessage

	@Serializable
	@SerialName("typing")
	data class Typing(
		val chatId: String,
		val userId: String,
		val active: Boolean,
	) : IncomingMessage

	@Serializable
	@SerialName("call.signal")
	data class CallSignal(
		val callId: String,
		val userId: String,
	) : IncomingMessage

	@Serializable
	@SerialName("sync")
	data class Sync(
		val event: SyncEvent,
	) : IncomingMessage
}
