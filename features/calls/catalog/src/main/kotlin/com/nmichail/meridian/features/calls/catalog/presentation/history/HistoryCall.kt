package com.nmichail.meridian.features.calls.catalog.presentation.history

import com.nmichail.meridian.features.calls.catalog.domain.entity.CallDirection

data class HistoryCall(
	val name: String,
	val time: HistoryTime,
	val direction: CallDirection,
	val live: Boolean,
	val duration: HistorySpan?,
	val personId: String,
	val chatId: String,
)
