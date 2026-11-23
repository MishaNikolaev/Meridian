package com.nmichail.meridian.features.calls.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CallHistoryPageDto(
	val items: List<CallHistoryItemDto>,
	val nextPage: String?,
)