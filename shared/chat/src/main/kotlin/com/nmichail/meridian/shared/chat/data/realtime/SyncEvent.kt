package com.nmichail.meridian.shared.chat.data.realtime

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class SyncEvent(
	val id: Long,
	val type: String,
	val payload: JsonObject,
	val createdAt: String,
)
