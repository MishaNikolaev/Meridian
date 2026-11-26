package com.nmichail.meridian.shared.chat.data.datasource

import kotlinx.serialization.Serializable

@Serializable
internal data class ChatRecords(
	val items: List<ChatRecord>,
)
